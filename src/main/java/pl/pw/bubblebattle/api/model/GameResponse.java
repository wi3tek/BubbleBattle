package pl.pw.bubblebattle.api.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.pw.bubblebattle.api.model.enums.GameStage;
import pl.pw.bubblebattle.api.model.enums.RoundStage;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GameResponse extends BaseResponse {

    private String gameId;
    private String name;
    private LocalDate date;
    private GameStage gameStage;
    private RoundStage roundStage;
    private List<TeamData> teams;
    private Integer bubbleStakes;

}
