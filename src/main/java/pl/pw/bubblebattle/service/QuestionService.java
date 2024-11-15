package pl.pw.bubblebattle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.pw.bubblebattle.api.model.CategoryData;
import pl.pw.bubblebattle.api.model.GameResponse;
import pl.pw.bubblebattle.infrastructure.ExcelReader;
import pl.pw.bubblebattle.storage.documents.Question;
import pl.pw.bubblebattle.storage.repository.QuestionRepo;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepo questionRepo;

    public void save(MultipartFile file) throws IOException {
        List<Question> questionList = ExcelReader.excelToQuestionList( file.getInputStream() );
        questionRepo.saveAll( questionList );
    }

    public void save(Question question)  {
        questionRepo.save( question );
    }

    public void prepareQuestionsAndCategories(GameResponse gameResponse) {
        Map<String, Integer> categoryMap = new HashMap<>();
        questionRepo.findAll().stream()
                .filter( question -> !question.isUsed() )
                .forEach( question -> categoryMap.merge( question.getCategory(), 1, Integer::sum ) );
        gameResponse.setCategoryList( prepareCategoryList( categoryMap ) );
    }

    private List<CategoryData> prepareCategoryList(Map<String, Integer> categoryMap) {
        return  categoryMap.entrySet().stream()
                .map( entry -> CategoryData.builder()
                        .value( entry.getKey() )
                        .count( entry.getValue() )
                        .build() )
                .sorted( Comparator.comparing( CategoryData::getValue ))
                .toList();
    }
}
