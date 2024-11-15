package pl.pw.bubblebattle.storage.documents;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Stakes  {

    private int bubbleAmount;
    private Team auctionWinner;

}
