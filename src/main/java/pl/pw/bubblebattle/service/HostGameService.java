package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.api.model.TeamData;
import pl.pw.bubblebattle.api.model.actions.AnswerTheQuestionRequest;
import pl.pw.bubblebattle.api.model.actions.ChooseCategoryRequest;
import pl.pw.bubblebattle.api.model.actions.PerformActionRequest;
import pl.pw.bubblebattle.api.model.actions.SellAnswersRequest;
import pl.pw.bubblebattle.api.model.enums.*;
import pl.pw.bubblebattle.infrastructure.SseEmitterManager;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.service.mapper.BubbleBattleMapper;
import pl.pw.bubblebattle.storage.documents.Game;
import pl.pw.bubblebattle.storage.documents.Question;
import pl.pw.bubblebattle.storage.documents.Team;
import pl.pw.bubblebattle.storage.service.GameDatabaseService;
import pl.pw.bubblebattle.storage.service.QuestionDatabaseService;

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

    @Transactional
    public synchronized GameResponse startGame(String gameId) {
        Game game = this.databaseService.read( gameId );
        validatorService.validateGameBeforeAction( Action.START_GAME,game );
        game.getTeams()
                .forEach( team -> team.setBubbleAmount( 10000 ) );
        game.setRoundStage( RoundStage.CATEGORY_SELECTION.name() );
        game.incrementRoundNumber();

        GameResponse gameResponse = mapper.map( game );
        databaseService.save( game );
        SseEmitterManager.sendSseEventToClients( gameId, gameResponse );
        gameResponse.setHostActions( hostActionService.prepareActions( gameResponse ) );
        questionService.prepareQuestionsAndCategories( gameResponse );
        return gameResponse;
    }

    @Transactional
    public GameResponse chooseCategory(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(),game );
        game.setRoundStage( RoundStage.BEFORE_AUCTION.name() );
        ChooseCategoryRequest data = (ChooseCategoryRequest) request;
        game.setCurrentCategory( data.getCategory() );
        databaseService.save( game );

        return prepareGameResponse( game );
    }

    @Transactional
    public GameResponse startAuction(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(),game );
        game.setRoundStage( RoundStage.AUCTION.name() );
        game.startAuction();
        databaseService.save( game );

        return prepareGameResponse( game );
    }

    @Transactional
    public GameResponse finishAuction(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(),game );
        game.setRoundStage( RoundStage.AUCTION_COMPLETE.name() );


        if(game.getTeams().stream().filter( Team::isActive ).filter( team ->  team.getBubbleStakesAmount() == game.getHighestBidAmount()  ).count() > 1) {
            throw new BubbleBattleException( "More than one team has the same bid amount" );
        }

        Team auctionWinner = game.getTeams().stream()
                .filter( Team::isActive )
                .filter( team -> team.getBubbleStakesAmount() == game.getHighestBidAmount() )
                .findFirst()
                .orElseThrow( () -> new BubbleBattleException( "Auction winner not found" ) );
        game.getStakes().setAuctionWinner( auctionWinner );

        if(SpecialCategory.getByValue( game.getCurrentCategory()).isPresent() ) {
            updateGameForSpecialCategories(game);
        }

        databaseService.save( game );



        return prepareGameResponse( game );
    }


    private GameResponse prepareGameResponse(Game game) {
        GameResponse gameResponse = mapper.map( game );
        gameResponse.sortActiveByOrder();
        gameResponse.markHighestStakes( gameResponse.getTeams() );
        SseEmitterManager.sendSseEventToClients( game.getId(), gameResponse );

        if(RoundStage.ROUND_SUMMARY.equals( gameResponse.getRoundStage() )) {
            questionService.prepareQuestionsAndCategories( gameResponse );
        }

        gameResponse.setHostActions( hostActionService.prepareActions( gameResponse ) );
        Optional.ofNullable(gameResponse.getAuctionWinner()).ifPresent( TeamData::shuffleAnswers );
        return gameResponse;
    }

    @Transactional
    public GameResponse randomQuestion(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(),game );
        game.setRoundStage( RoundStage.BEFORE_QUESTION.name() );

        List<Question> questions = questionDatabaseService.getQuestions( game.getCurrentCategory() );
        validateQuestions( questions, game );

        game.getStakes().getAuctionWinner().setActiveQuestion( randomQuestion(questions ) );
        databaseService.save( game );
        return prepareGameResponse( game );
    }

    private Question randomQuestion(List<Question> questions) {
        int questionIndex = RANDOM.ints( 0, questions.size() )
                .findFirst()
                .orElseThrow(() -> new BubbleBattleException( "Cannot random question" ));

        Question question = questions.get( questionIndex );
        question.setUsed( true );
        questionService.save(question);
        return question;
    }

    private void validateQuestions(List<Question> questions, Game game) {
        if(questions.isEmpty()) {
            throw new BubbleBattleException(String.format(
                    "There is no questions possible to use incategory: '%s' ",
                    game.getCurrentCategory()
            ));
        }
    }

    @Transactional
    public GameResponse showQuestion(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(),game );
        game.setRoundStage( RoundStage.QUESTION.name() );
        databaseService.save( game );
        return prepareGameResponse( game );
    }

    @Transactional
    public GameResponse sellAnswers(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(),game );
        game.setRoundStage( RoundStage.QUESTION_WITH_PROMPTS.name() );
        SellAnswersRequest data = (SellAnswersRequest) request;
        Team auctionWinner =
                Optional.ofNullable(  game.getStakes().getAuctionWinner()).orElseThrow(() -> new BubbleBattleException( "There is no auctionWinner" ));
        Team purchaser = game.getTeams().stream()
                .filter( team -> team.getColor().equals( data.getTeamColor().name() )
                        && auctionWinner.getColor().equals( data.getTeamColor().name() ) )
                .findAny()
                .orElseThrow( () -> new BubbleBattleException( "Team data is not match to auctionWinner" ) );
        validateBeforeSell( data,purchaser);

        game.subtractBubbles(data.getTeamColor(), data.getPrice());
        game.getStakes().getAuctionWinner().subtractBubbles(data.getPrice());
        databaseService.save( game );
        return prepareGameResponse( game );
    }

    private void validateBeforeSell( SellAnswersRequest data, Team purchaser) {
        if(purchaser.getBubbleAmount() < data.getPrice()) {
            throw new BubbleBattleException( String.format(
                    "%s's team do not have enough bubbles (%d) to buy prompts for %d",
                    purchaser.getColor(),
                    purchaser.getBubbleAmount(),
                    data.getPrice()
            ) );
        }
    }

    private void updateGameForSpecialCategories(Game game) {
        log.info("Action ANSWER_THE_QUESTION performed");
        game.setRoundStage( RoundStage.AFTER_ANSWER.name() );
        game.setCurrentCategory( null );
        updateGameAfterWrong(game);
        game.getStakes().setBubbleAmount( 0 );
        game.getStakes().setAuctionWinner( null );
    }

    @Transactional
    public GameResponse answerTheQuestion(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(),game );
        game.setRoundStage( RoundStage.AFTER_ANSWER.name() );
        game.setCurrentCategory( null );
        AnswerTheQuestionRequest data = (AnswerTheQuestionRequest) request;

        String auctionWinnerColor = game.getStakes().getAuctionWinner().getColor();
        if(!data.getTeamColor().name().equals( auctionWinnerColor )) {
            throw new BubbleBattleException(String.format(
                    "Auction winner color (%s) it not match requested teamColor %s",
                    auctionWinnerColor,
                    data.getTeamColor() )
            );
        }

        if(AnswerType.CORRECT.equals( data.getAnswer() )) {
            updateGameAfterCorrect(game,data.getTeamColor());
        } else {
            updateGameAfterWrong(game);
        }
        databaseService.save( game );
        return prepareGameResponse( game );
    }

    private void updateGameAfterWrong(Game game) {
        clearActiveQuestion( game );
        game.resetStakes();
    }

    private void updateGameAfterCorrect(Game game, TeamColor teamColor) {
        int bubbleAmount = game.getStakes().getBubbleAmount();
        clearActiveQuestion( game );
        game.getStakes().setBubbleAmount( 0 );
        game.updateTeamBubbles(teamColor,bubbleAmount );
        game.resetStakes();
    }

    private void clearActiveQuestion(Game game) {
        Optional.ofNullable( game.getStakes().getAuctionWinner()).ifPresent( x ->x.setActiveQuestion( null ) );
    }

    @Transactional
    public GameResponse finishRound(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(),game );
        game.setRoundStage( RoundStage.ROUND_SUMMARY.name() );
        game.checkTeamsAfterRound();
        game.incrementRoundNumber();
        databaseService.save( game );
        return prepareGameResponse( game );
    }

    @Transactional
    public GameResponse goToTheFinal(PerformActionRequest request) {
        Game game = this.databaseService.read( request.getGameId() );
        validatorService.validateGameBeforeAction( request.getAction(),game );
        game.setGameStage( GameStage.FINAL.name() );
        game.setRoundStage( RoundStage.ROUND_SUMMARY.name() );
        game.setRoundNumber( 1 );
        game.prepareTeamsToFinal();
        game.getStakes().setBubbleAmount( 0 );
        databaseService.save( game );
                return prepareGameResponse( game );
    }
}
