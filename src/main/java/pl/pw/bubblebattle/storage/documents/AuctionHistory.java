package pl.pw.bubblebattle.storage.documents;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@Document
public class AuctionHistory {

    @Id
    private String historyId;
    private String previousHistoryId;
    private String nextHistoryId;
    private String auctionId;
    private String gameId;
    private int highestBidAmount;
    private List<AuctionHistoryItem> auctionHistoryItemList;

    @CreatedDate
    private LocalDateTime creationDate;

    @LastModifiedDate
    private LocalDateTime modificationDate;

    @CreatedBy
    private String createdBy;

}
