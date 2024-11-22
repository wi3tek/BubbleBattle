package pl.pw.bubblebattle.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pw.bubblebattle.api.model.enums.AuctionHistoryOption;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReverseRestoreAuctionRequest {

    private String gameId;
    private AuctionHistoryOption option;
}
