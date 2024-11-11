package pl.pw.bubblebattle.storage.service;

import com.mongodb.MongoWriteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.storage.documents.Game;
import pl.pw.bubblebattle.storage.repository.GameRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameDatabaseService {

    private final GameRepo gameRepo;

    @Transactional
    public Game save(Game game) {
        try {
            return saveGame( game );
        } catch (MongoWriteException e) {
            throw new BubbleBattleException( "Błąd podczas zapisu\n" + e.getMessage() );
        }
    }

    private Game saveGame(Game game) {
        return game.getId() == null
                ? gameRepo.insert( game )
                : gameRepo.save( game );
    }

    public Game read(String gameId) {
        return gameRepo.findById( gameId ).orElseThrow( () -> new BubbleBattleException( "Not found" ) );
    }

    public List<Game> getAllGames() {
        return gameRepo.findAll();
    }
}
