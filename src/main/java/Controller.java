import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.*;

public class Controller {

    @FXML
    private VBox node;

    private DataFormatter dataFormatter;

    private FileChooser fileChooser;

    public Controller() {
        this.fileChooser = new FileChooser();
        this.dataFormatter = new DataFormatter();
    }

    @FXML
    protected void handleButtonAction(ActionEvent event) {
        File file = this.openFolder();
        this.ReadFromExcel(file.getAbsoluteFile());
    }

    private File openFolder() {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Excel", "*.xls", "*.xlsx")
        );
        return fileChooser.showOpenDialog(node.getScene().getWindow());
    }

    private void ReadFromExcel(File fileName) {
        try {
            Workbook workbook = WorkbookFactory.create(fileName);
            workbook.forEach(sheet -> {
                System.out.println("=> " + sheet.getSheetName());
                sheet.forEach(row -> {
                    row.forEach(cell -> {
                        String cellValue = dataFormatter.formatCellValue(cell);
                        System.out.print(cellValue + "\t");
                    });
                    System.out.println();
                });
            });
            workbook.close();
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }
}
