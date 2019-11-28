package interfaces.workbook;

import domain.DataService;
import domain.model.PingMianXianXing;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileService implements DataService {

    private DataFormatter dataFormatter;

    private FileChooser fileChooser;

    public FileService() {
        this.fileChooser = new FileChooser();
        this.dataFormatter = new DataFormatter();
    }

    public File openFolder(VBox node) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Excel", "*.xls", "*.xlsx")
        );
        return fileChooser.showOpenDialog(node.getScene().getWindow());
    }

    @Override
    public List<PingMianXianXing> getPingMianXianXingData(VBox node) {
        List<PingMianXianXing> pingMianXianXings = new ArrayList<>();
        try {
            Workbook workbook = WorkbookFactory.create(this.openFolder(node));
            for (Row row : workbook.getSheetAt(0)) {
                if (dataFormatter.formatCellValue(row.getCell(0)).equals("")) {
                    break;
                }
                if (row.getRowNum() != 0) {
                    pingMianXianXings.add(PingMianXianXing.builder()
                            .start(Double.valueOf(dataFormatter.formatCellValue(row.getCell(0))))
                            .end(Double.valueOf(dataFormatter.formatCellValue(row.getCell(1))))
                            .radius(Double.valueOf(dataFormatter.formatCellValue(row.getCell(2))))
                            .build());
                }
            }
            workbook.close();
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
        return pingMianXianXings;
    }
}
