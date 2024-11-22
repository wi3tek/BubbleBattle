package pl.pw.bubblebattle.storage.documents;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuctionHistoryItem {

    private String teamColor;
    private int bubblesAmount;
    private int bidAmount;
    private String category;

}
