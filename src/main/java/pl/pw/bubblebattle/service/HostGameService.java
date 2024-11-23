package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pw.bubblebattle.api.model.CorrectBubblesRequest;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.api.model.ReverseRestoreAuctionRequest;
import pl.pw.bubblebattle.api.model.TeamData;
import pl.pw.bubblebattle.api.model.actions.*;
import pl.pw.bubblebattle.api.model.enums.*;
import pl.pw.bubblebattle.infrastructure.SseEmitterManager;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.service.mapper.BubbleBattleMapper;
import pl.pw.bubblebattle.storage.documents.Game;
import pl.pw.bubblebattle.storage.documents.Question;
import pl.pw.bubblebattle.storage.documents.Stakes;
import pl.pw.bubblebattle.storage.documents.Team;
import pl.pw.bubblebattle.storage.service.AuctionDatabaseService;
import pl.pw.bubblebattle.storage.service.GameDatabaseService;
import pl.pw.bubblebattle.storage.service.QuestionDatabaseService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class HostGameService {

    public static final Random RANDOM = new Random();
    private final GameDatabaseService databaseService;
    private final QuestionDatabaseService questionDatabaseService;
    private final BubbleBattleMapper mapper = Mappers.getMapper( BubbleBattleMapper.class );
    private final ValidatorService validatorService;
    private final HostActionService hostActionService;
    private final QuestionService questionService;
    private final AuctionDatabaseService auctionDatabaseService;

    @Transactional
    public synchronized GameResponse startGame(String gameId) {
        Game game = this.databaseService.read( gameId );
        validatorService.validateGameBeforeAction( Action.START_GAME, game );
        game.setRoundStage( RoundStage.ROUND_SUMMARY.name() );
        GameResponse gameResponse = mapper.map( game );
        databaseService.save( game );
        gameResponse.sortActiveByOrder();
        SseEmitterManager.sendSseEventToClients( gameId, gameResponse );
        gameResponse.setHostActions( hostActionService.prepareActions( gameResponse ) );
        questionService.prepareQuestionsAndCategories( gameResponse );
        return gameResponse;
    }


    @Transactional
    public synchronized GameResponse initBubbles(String gameId) {
        Game game = this.databaseService.read( gameId );
        validatorService.validateGameBeforeAction( Action.INIT_BUBBLES, game );
        game.getTeams()
                .forEach( team -> team.setBubbleAmount( 10000 ) );
        game.setRoundStage( RoundStage.CATEGORY_SELECTION.name() );
        game.incrementRoundNumber();

        GameResponse gameResponse = mapper.map( game );
        databaseService.save( game );
        gameResponse.sortActiveByOrder();
        gameResponse.setMoneyUp( true );
        SseEmitterManager.sendSseEventToClients( gameId, gameResponse );
        gameResponse.setHostActions( hostActionService.prepareActions( gameResponse ) );
        questionService.prepareQuestionsAndCategories( gameResponse );

        return gameResponse;
    }

    @Transactional
    public GameResponse chooseCategory(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(), game );

        if (game.getTeams().stream().filter( Team::isActive ).count() < 2) {
            throw new BubbleBattleException(
                    "It's not possible to perform new round without at least two active teams in the game" );
        }

        game.setRoundStage( RoundStage.BEFORE_AUCTION.name() );
        ChooseCategoryRequest data = (ChooseCategoryRequest) request;
        game.setCurrentCategory( data.getCategory() );
        databaseService.save( game );

        return prepareGameResponse( game, false );
    }

    @Transactional
    public GameResponse startAuction(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        game.setCurrentAuctionHistory( null );
        validatorService.validateGameBeforeAction( request.getAction(), game );
        game.setRoundStage( RoundStage.AUCTION.name() );
        game.startAuction();

        auctionDatabaseService.saveHistory( game, AuctionHistoryOption.INSERT );

        databaseService.save( game );

        return prepareGameResponse( game, true );
    }

    @Transactional
    public GameResponse finishAuction(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        updateGame( request, game );
        databaseService.save( game );
        return prepareGameResponse( game, false );
    }

    public void updateGame(PerformActionRequest request, Game game) {
        validatorService.validateGameBeforeAction( request.getAction(), game );

        if (game.getTeams().stream().filter( Team::isActive ).filter( team -> team.getBubbleStakesAmount() == game.getHighestBidAmount() ).count() > 1) {
            throw new BubbleBattleException( "More than one team has the same bid amount" );
        }

        Team auctionWinner = game.getTeams().stream()
                .filter( Team::isActive )
                .filter( team -> team.getBubbleStakesAmount() == game.getHighestBidAmount() )
                .findFirst()
                .orElseThrow( () -> new BubbleBattleException( "AuctionHistory winner not found" ) );
        game.getStakes().setAuctionWinner( auctionWinner );

        if (SpecialCategory.getByValue( game.getCurrentCategory() ).isPresent()) {
            updateGameForSpecialCategories( game );
        } else {
            game.setRoundStage( RoundStage.AUCTION_COMPLETE.name() );
        }
    }

    private GameResponse prepareGameResponse(Game game, boolean moneyUp) {
        GameResponse gameResponse = mapper.map( game );
        gameResponse.sortActiveByOrder();
        gameResponse.markHighestStakes( gameResponse.getTeams() );
        gameResponse.setMoneyUp( moneyUp );
        SseEmitterManager.sendSseEventToClients( game.getId(), gameResponse );

        if (RoundStage.ROUND_SUMMARY.equals( gameResponse.getRoundStage() )) {
            questionService.prepareQuestionsAndCategories( gameResponse );
        }

        gameResponse.setHostActions( hostActionService.prepareActions( gameResponse ) );
        Optional.ofNullable( gameResponse.getAuctionWinner() ).ifPresent( TeamData::shuffleAnswers );

        return gameResponse;
    }

    @Transactional
    public GameResponse randomQuestion(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(), game );
        game.setRoundStage( RoundStage.BEFORE_QUESTION.name() );

        List<Question> questions = questionDatabaseService.getQuestions( game.getCurrentCategory() );
        validateQuestions( questions, game );

        game.getStakes().getAuctionWinner().setActiveQuestion( randomQuestion( questions ) );
        databaseService.save( game );
        return prepareGameResponse( game, false );
    }

    private Question randomQuestion(List<Question> questions) {
        int questionIndex = RANDOM.ints( 0, questions.size() )
                .findFirst()
                .orElseThrow( () -> new BubbleBattleException( "Cannot random question" ) );

        Question question = questions.get( questionIndex );
        question.setUsed( true );
        questionService.save( question );
        question.setRemainingTimeSec( 60 );
        return question;
    }

    private void validateQuestions(List<Question> questions, Game game) {
        if (questions.isEmpty()) {
            throw new BubbleBattleException( String.format(
                    "There is no questions possible to use incategory: '%s' ",
                    game.getCurrentCategory()
            ) );
        }
    }

    @Transactional
    public GameResponse showQuestion(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(), game );
        game.setRoundStage( RoundStage.QUESTION.name() );
        game.getStakes().getAuctionWinner().getActiveQuestion().setStartOnInit( true );
        databaseService.save( game );
        return prepareGameResponse( game, false );
    }

    @Transactional
    public GameResponse sellAnswers(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(), game );
        game.setRoundStage( RoundStage.QUESTION_WITH_PROMPTS.name() );
        SellAnswersRequest data = (SellAnswersRequest) request;
        Team auctionWinner =
                Optional.ofNullable( game.getStakes().getAuctionWinner() ).orElseThrow( () -> new BubbleBattleException( "There is no auctionWinner" ) );
        Team purchaser = game.getTeams().stream()
                .filter( team -> team.getColor().equals( data.getTeamColor().name() )
                        && auctionWinner.getColor().equals( data.getTeamColor().name() ) )
                .findAny()
                .orElseThrow( () -> new BubbleBattleException( "Team data is not match to auctionWinner" ) );
        validateBeforeSell( data, purchaser );

        auctionWinner.getActiveQuestion().setRemainingTimeSec( 60 );
        auctionWinner.getActiveQuestion().setStartOnInit( true );

        game.subtractBubbles( data.getTeamColor(), data.getPrice() );
        game.getStakes().getAuctionWinner().subtractBubbles( data.getPrice() );
        databaseService.save( game );
        return prepareGameResponse( game, false );
    }

    private void validateBeforeSell(SellAnswersRequest data, Team purchaser) {
        if (purchaser.getBubbleAmount() < data.getPrice()) {
            throw new BubbleBattleException( String.format(
                    "%s's team do not have enough bubbles (%d) to buy prompts for %d",
                    purchaser.getColor(),
                    purchaser.getBubbleAmount(),
                    data.getPrice()
            ) );
        }
    }

    private void updateGameForSpecialCategories(Game game) {
        log.info( "Action ANSWER_THE_QUESTION performed" );
        game.setRoundStage( RoundStage.AFTER_ANSWER.name() );
        updateGameAfterWrong( game );
        game.getStakes().setBubbleAmount( 0 );
    }

    @Transactional
    public GameResponse updateQuestionResult(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(), game );
        game.setRoundStage( RoundStage.AFTER_ANSWER.name() );
        AnswerTheQuestionRequest data = (AnswerTheQuestionRequest) request;

        String auctionWinnerColor = game.getStakes().getAuctionWinner().getColor();
        if (!data.getTeamColor().name().equals( auctionWinnerColor )) {
            throw new BubbleBattleException( String.format(
                    "AuctionHistory winner color (%s) it not match requested teamColor %s",
                    auctionWinnerColor,
                    data.getTeamColor() )
            );
        }

        if (AnswerType.CORRECT.equals( data.getAnswer() )) {
            updateGameAfterCorrect( game, data.getTeamColor() );
        } else {
            updateGameAfterWrong( game );
        }

        game.getStakes().getAuctionWinner().getActiveQuestion().setRemainingTimeSec( 0 );
        game.getStakes().getAuctionWinner().getActiveQuestion().setStartOnInit( false );
        databaseService.save( game );
        return prepareGameResponse( game, false );
    }

    private void updateGameAfterWrong(Game game) {
        updateQuestionResult( game, false );
        game.resetStakes();
    }

    private void updateGameAfterCorrect(Game game, TeamColor teamColor) {
        int bubbleAmount = game.getStakes().getBubbleAmount();
        game.updateTeamBubbles( teamColor, bubbleAmount );
        updateQuestionResult( game, true );
        game.resetStakes();
    }

    private void updateQuestionResult(Game game, boolean answerValue) {
        Optional.ofNullable( game.getStakes().getAuctionWinner() ).ifPresent( team -> team.answer( answerValue ) );
    }

    private void clearActiveQuestion(Game game) {
        Optional.ofNullable( game.getStakes().getAuctionWinner() ).ifPresent( x -> {
            if (SpecialCategory.getByValue( game.getCurrentCategory() ).isPresent() &&
                    Optional.ofNullable( x.getActiveQuestion() ).map( Question::isAnsweredCorrect ).orElse( false )) {
                game.getStakes().setBubbleAmount( 0 );
            }
            x.setActiveQuestion( null );
        } );
    }

    @Transactional
    public GameResponse finishRound(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(), game );

        game.setCurrentCategory( null );
        Optional<Team> auctionWinner = Optional.ofNullable( game.getStakes().getAuctionWinner() );

        auctionWinner
                .flatMap( team -> game.getTeams()
                        .stream()
                        .filter( t -> t.getColor().equals( team.getColor() ) )
                        .findFirst()
                        .flatMap( t -> Optional.ofNullable( team.getActiveQuestion() ) ) )
                .ifPresent( q -> {
                    if (q.isAnsweredCorrect()) {
                        game.getStakes().setBubbleAmount( 0 );
                    }
                } );
        clearActiveQuestion( game );

        game.setRoundStage( RoundStage.ROUND_SUMMARY.name() );
        game.checkTeamsAfterRound();
        game.incrementRoundNumber();
        databaseService.save( game );
        return prepareGameResponse( game, game.getStakes().getBubbleAmount() == 0    );
    }

    @Transactional
    public GameResponse goToTheFinal(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(), game );
        game.setGameStage( GameStage.FINAL.name() );
        game.setRoundStage( RoundStage.ROUND_SUMMARY.name() );
        game.setRoundNumber( 1 );
        game.prepareTeamsToFinal();
        game.getStakes().setBubbleAmount( 0 );
        databaseService.save( game );
        return prepareGameResponse( game, false );
    }

    @Transactional
    public void correctBubbles(CorrectBubblesRequest request) {
        String gameId = request.getGameId();
        Game game = databaseService.read( gameId );

        if (request.getTeamColor().equals( "STAKES" )) {
            updateStakesAmount( game.getStakes(), request.getBubblesAmount() );
        } else {
            game.getTeams().stream()
                    .filter( t -> t.isActive() && t.getColor().equals( request.getTeamColor() ) )
                    .findFirst()
                    .ifPresent( team -> updateTeamBubbles( team, request.getBubblesAmount() ) );
        }

        databaseService.save( game );

        GameResponse gameResponse = mapper.map( game );
        gameResponse.sortActiveByOrder();
        SseEmitterManager.sendSseEventToClients( gameId, gameResponse );
    }

    private void updateStakesAmount(Stakes stakes, int bubblesAmount) {
        int newBubblesAmount = stakes.getBubbleAmount() + bubblesAmount;
        stakes.setBubbleAmount( Math.max( newBubblesAmount, 0 ) );
    }

    private void updateTeamBubbles(Team team, int bubblesAmount) {
        int newBubblesAmount = team.getBubbleAmount() + bubblesAmount;
        team.setBubbleAmount( Math.max( newBubblesAmount, 0 ) );
    }

    @Transactional
    public GameResponse finishGame(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(), game );

        Optional<Team> optionalWinner = game.getTeams().stream()
                .filter( Team::isActive )
                .max( Comparator.comparing( Team::getBubbleAmount ) );

        if (optionalWinner.isEmpty()) {
            throw new BubbleBattleException( "Cannot find winner" );
        }

        Team winner = optionalWinner.get();

        game.getTeams().stream()
                .filter( team -> !team.getColor().equals( winner.getColor() ) )
                .forEach( team -> team.setActive( false ) );


        winner.setBubbleAmount( winner.getBubbleAmount() + game.getStakes().getBubbleAmount() );
        game.getStakes().setBubbleAmount( 0 );
        game.getStakes().setAuctionWinner( null );
        game.updateTeam( winner );
        game.setRoundStage( RoundStage.GAME_FINISHED.name() );

        databaseService.save( game );
        return prepareGameResponse( game, false );
    }

    @Transactional
    public void reverseRestoreAuction(ReverseRestoreAuctionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( Action.REVERSE_RESTORE_AUCTION, game );
        auctionDatabaseService.saveHistory( game, request.getOption() );
        game.updateTeamsByCurrentHistory();

        databaseService.save( game );
        GameResponse gameResponse = mapper.map( game );
        gameResponse.markHighestStakes( gameResponse.getTeams() );
        SseEmitterManager.sendSseEventToClients( game.getId(), gameResponse );
    }

    @Transactional
    public GameResponse startStopQuestionTimer(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( Action.START_STOP_QUESTION_TIMER, game );

        StartStopQuestionTimerRequest data = (StartStopQuestionTimerRequest) request;


        game.getStakes().getAuctionWinner().getActiveQuestion().setRemainingTimeSec( data.getSecondsRemaining() );
        game.getStakes().getAuctionWinner().getActiveQuestion().setStartOnInit( data.isStart() );


        databaseService.save( game );
        return prepareGameResponse( game, false );

    }
}
