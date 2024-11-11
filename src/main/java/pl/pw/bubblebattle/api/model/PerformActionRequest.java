package pl.pw.bubblebattle.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pw.bubblebattle.api.model.enums.Action;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformActionRequest {

    private String gameId;
    private Action action;
}
