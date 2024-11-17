package pl.pw.bubblebattle.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.pw.bubblebattle.api.model.enums.GameStage;
import pl.pw.bubblebattle.api.model.enums.RoundStage;
import pl.pw.bubblebattle.api.model.enums.TeamColor;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GameResponse extends BaseResponse {

    private static final List<TeamColor> REGULAR_GAME_STAGE_TEAM_COLORS = List.of(
            TeamColor.BLUE,
            TeamColor.GREEN,
            TeamColor.YELLOW
    );

    private String gameId;
    private String name;
    private LocalDateTime date;
    private GameStage gameStage;
    private RoundStage roundStage;
    private int roundNumber;
    private List<TeamData> teams;
    private Integer bubbleStakes;
    private List<HostAction> hostActions;
    private List<CategoryData> categoryList;
    private String currentCategory;
    private int highestBidAmount;
    private TeamData auctionWinner;

    public void markHighestStakes(List<TeamData> teamData) {
        int highestStakes = teamData.stream()
                .map( TeamData::getBubbleStakesAmount ).max( Integer::compareTo ).orElse( 0 );
        teamData.forEach( team -> {
                    if (team.getBubbleStakesAmount() == highestStakes)
                        team.setHighestStakes( true );

                    if (highestStakes == 0 || highestStakes == 500) {
                        team.setHighestStakes( false );
                    }

                }
        );


        this.teams = teamData;
        this.sortActiveByOrder();
    }

    public void sortActiveByOrder() {
        Predicate<TeamData> predicate = x -> {
            if(this.gameStage.equals( GameStage.REGULAR )) {
                return REGULAR_GAME_STAGE_TEAM_COLORS.contains( x.getTeamColor() );
            }

            return x.isActive();
        };
        this.setTeams( this.teams.stream()
                .filter( predicate )
                .sorted( Comparator.comparing( t -> t.getTeamColor().getOrder() ) )
                .toList()
        );
    }
}
