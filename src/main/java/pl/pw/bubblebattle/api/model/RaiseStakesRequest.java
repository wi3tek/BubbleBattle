package pl.pw.bubblebattle.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pw.bubblebattle.api.model.enums.TeamColor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaiseStakesRequest {

    private String gameId;
    private TeamColor teamColor;
    private int bubblesAmount;
    private boolean finalBid;
}
