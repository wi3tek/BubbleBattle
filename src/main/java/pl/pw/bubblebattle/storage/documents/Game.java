package pl.pw.bubblebattle.storage.documents;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document("Game")
@Data
@Builder(toBuilder = true)
public class Game {

    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private String gameStage;
    private int roundNumber;
    private String roundStage;
    private List<Team> teams;
    private Stakes stakes;

    @CreatedDate
    private LocalDateTime creationDate;

    @LastModifiedDate
    private LocalDateTime modificationDate;

    @CreatedBy
    private String createdBy;


    public void updateStakes(int bubblesAmount) {
        this.stakes.setBubbleAmount( this.stakes.getBubbleAmount() + bubblesAmount);
    }

    public void updateTeam(Team team) {
        List<Team> objectTeams = new ArrayList<>(this.teams);

        Team teamT = this.teams.stream().filter( t -> t.getColor().equals( team.getColor() ) )
                .findFirst()
                .orElseThrow(() -> new BubbleBattleException( "wront team color "+team.getColor() ) ) ;
        objectTeams.remove( teamT );
        objectTeams.add( team );
        this.teams = objectTeams;
    }

    public void reset() {
        this.stakes.setBubbleAmount( 0 );
        this.teams.forEach( team -> {
            team.setBubbleAmount( 10000 );
            team.setBubbleStakesAmount( 0 );
        } );
    }
}
