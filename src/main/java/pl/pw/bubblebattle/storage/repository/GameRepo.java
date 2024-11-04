package pl.pw.bubblebattle.storage.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.pw.bubblebattle.storage.documents.Game;

import java.util.Optional;

@Repository
public interface GameRepo extends MongoRepository<Game, Integer> {


    Optional<Game> findById(String id);

}
