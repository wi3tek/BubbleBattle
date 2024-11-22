package pl.pw.bubblebattle.infrastructure;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import pl.pw.bubblebattle.infrastructure.exception.BubbleBattleException;
import pl.pw.bubblebattle.storage.documents.Answer;
import pl.pw.bubblebattle.storage.documents.Question;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@UtilityClass
public class ExcelReader {

    private static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String SHEET = "PYTANIA";

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals( file.getContentType() );
    }

    public static List<Question> excelToQuestionList(InputStream is) {
        Workbook workbook;

        try {
            workbook = new XSSFWorkbook( is );
        } catch (IOException e) {
            throw new BubbleBattleException( "Error during opening workbook", e );
        }

        Sheet sheet = workbook.getSheet( SHEET );
        Iterator<Row> rows = sheet.iterator();
        List<Question> questions = new ArrayList<>();

        int rowNumber = 0;

        while (rows.hasNext()) {
            Row currentRow = rows.next();
            // skip header
            if (rowNumber == 0) {
                rowNumber++;
                continue;
            }
            Iterator<Cell> cellsInRow = currentRow.iterator();
            Question question = new Question();
            int cellIdx = 0;
            while (cellsInRow.hasNext()) {
                Cell currentCell = cellsInRow.next();
                switch (cellIdx) {
                    case 0:
                        question.setExcelId( (int) currentCell.getNumericCellValue() );
                        break;
                    case 1:
                        question.setValue( currentCell.getStringCellValue() );
                        break;
                    case 2:
                        addAnswer( question, currentCell, true );
                        break;
                    case 3, 4, 5:
                        addAnswer( question, currentCell, false );
                        break;
                    case 6:
                        question.setCategory( currentCell.getStringCellValue() );
                        break;
                    case 7:
                        question.setImageUrl( currentCell.getStringCellValue() );
                        break;
                    case 8:
                        question.setHostFacts( currentCell.getStringCellValue() );
                        break;
                    default:
                        break;
                }
                cellIdx++;
            }

            question.setRemainingTimeSec( 0 );
            questions.add( question );
        }

        try {
            workbook.close();
        } catch (Exception e) {
            throw new BubbleBattleException( "Error during closing stream", e );
        }

        return questions;
    }

    private static void addAnswer(Question question, Cell currentCell, boolean isCorrect) {
        String stringCellValue;
        try {
            stringCellValue = currentCell.getStringCellValue();
        } catch (IllegalStateException e) {
            stringCellValue = currentCell.getNumericCellValue() + "";
        }

        question.addAnswer( Answer.builder()
                .value( stringCellValue )
                .correct( isCorrect )
                .build() );
    }
}
