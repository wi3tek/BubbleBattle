package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.pw.bubblebattle.infrastructure.ExcelReader;
import pl.pw.bubblebattle.storage.documents.Question;
import pl.pw.bubblebattle.storage.repository.QuestionRepo;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepo questionRepo;

    public void save(MultipartFile file) throws IOException {
        List<Question> questionList = ExcelReader.excelToQuestionList(file.getInputStream());
        questionRepo.saveAll( questionList );
    }
}
