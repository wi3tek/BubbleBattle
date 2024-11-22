package pl.pw.bubblebattle.storage.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.pw.bubblebattle.storage.documents.AuctionHistory;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepo extends MongoRepository<AuctionHistory, String> {

    Optional<AuctionHistory> findFirstByGameId(String gameId);
    List<AuctionHistory> findByAuctionId(String auctionId);
}
