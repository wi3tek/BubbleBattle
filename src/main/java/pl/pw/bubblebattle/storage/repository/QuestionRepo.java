package pl.pw.bubblebattle.storage.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.pw.bubblebattle.storage.documents.Question;

import java.util.List;

@Repository
public interface QuestionRepo extends MongoRepository<Question, String> {

    List<Question> findByCategory(String category);

}
