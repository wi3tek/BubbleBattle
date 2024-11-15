package pl.pw.bubblebattle.storage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pw.bubblebattle.storage.documents.Question;
import pl.pw.bubblebattle.storage.repository.QuestionRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionDatabaseService {

    private final QuestionRepo questionRepo;

    public List<Question> getQuestions(String category) {
        return questionRepo.findByCategory( category ).stream()
                .filter( question -> !question.isUsed() )
                .toList();
    }
}
