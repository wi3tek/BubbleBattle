package pl.pw.bubblebattle.storage.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.pw.bubblebattle.storage.documents.Question;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepo extends MongoRepository<Question, Integer> {

    Optional<Question> findById(String id);
    List<Question> findByCategory(String category);

}
