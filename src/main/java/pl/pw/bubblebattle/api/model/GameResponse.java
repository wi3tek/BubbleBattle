package pl.pw.bubblebattle.api.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.pw.bubblebattle.api.model.enums.GameStage;
import pl.pw.bubblebattle.api.model.enums.RoundStage;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GameResponse extends BaseResponse {

    private String gameId;
    private String name;
    private LocalDateTime date;
    private GameStage gameStage;
    private RoundStage roundStage;
    private int roundNumber;
    private List<TeamData> teams;
    private Integer bubbleStakes;
    private List<HostAction> hostActions;

    public void markHighestStakes(List<TeamData> teamData) {
        int highestStakes = teamData.stream().map( TeamData::getBubbleStakesAmount ).max( Integer::compareTo ).orElse( 0 );
        teamData.forEach( team -> {
                    if (team.getBubbleStakesAmount() == highestStakes)
                        team.setHighestStakes( true );

                    if(highestStakes==0) {
                        team.setHighestStakes( false );
                    }

                }
        );
        this.teams = teamData;
        this.sortByOrder();
    }

    public void sortByOrder() {
        this.teams.sort( Comparator.comparing( t -> t.getTeamColor().getOrder() ) );
    }

}
