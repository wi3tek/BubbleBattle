package pl.pw.bubblebattle.api.model.actions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class ChooseCategoryRequest extends PerformActionRequest {

    private String category;
}
