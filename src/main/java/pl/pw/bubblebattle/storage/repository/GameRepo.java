package pl.pw.bubblebattle.storage.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.pw.bubblebattle.storage.documents.Game;

@Repository
public interface GameRepo extends MongoRepository<Game, String> {

}
