package pl.pw.bubblebattle.api.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pw.bubblebattle.api.model.enums.Action;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostAction {

    private Action action;
    private String description;

}
