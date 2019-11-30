package interfaces.workbook;

import domain.DataService;
import domain.model.AggData;
import domain.model.GouZhaoWu;
import domain.model.PingMianXianXing;
import domain.model.ZongMianXianXing;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileService implements DataService {

    private DataFormatter dataFormatter;

    private FileChooser fileChooser;

    public FileService() {
        this.fileChooser = new FileChooser();
        this.dataFormatter = new DataFormatter();
    }

    private File openFolder(VBox node) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Excel", "*.xls", "*.xlsx")
        );
        return fileChooser.showOpenDialog(node.getScene().getWindow());
    }

    @Override
    public List<PingMianXianXing> getPingMianXianXingData(VBox node) throws IOException, InvalidFormatException {
        List<PingMianXianXing> pingMianXianXings = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(this.openFolder(node));
        for (Row row : workbook.getSheetAt(0)) {
            if (dataFormatter.formatCellValue(row.getCell(0)).equals("")) {
                break;
            }
            if (row.getRowNum() != 0) {
                pingMianXianXings.add(buildPingMianXianXing(row));
            }
        }
        workbook.close();
        return pingMianXianXings;
    }

    private PingMianXianXing buildPingMianXianXing(Row row) {
        return PingMianXianXing.builder()
                .start(Double.valueOf(dataFormatter.formatCellValue(row.getCell(0))))
                .end(Double.valueOf(dataFormatter.formatCellValue(row.getCell(1))))
                .radius(Double.valueOf(dataFormatter.formatCellValue(row.getCell(2))))
                .build();
    }

    @Override
    public List<ZongMianXianXing> getZongMianXianXingData(VBox node) throws IOException, InvalidFormatException {
        List<ZongMianXianXing> zongMianXianXings = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(this.openFolder(node));
        for (Row row : workbook.getSheetAt(0)) {
            if (dataFormatter.formatCellValue(row.getCell(0)).equals("")) {
                break;
            }
            if (row.getRowNum() != 0) {
                zongMianXianXings.add(buildZongMianXianXing(row));
            }
        }
        workbook.close();
        return zongMianXianXings;
    }

    private ZongMianXianXing buildZongMianXianXing(Row row) {
        return ZongMianXianXing.builder()
                .start(Double.valueOf(dataFormatter.formatCellValue(row.getCell(0))))
                .end(Double.valueOf(dataFormatter.formatCellValue(row.getCell(1))))
                .slope(Double.valueOf(dataFormatter.formatCellValue(row.getCell(2))))
                .build();
    }

    @Override
    public List<GouZhaoWu> getGouZhaoWuData(VBox node) throws IOException, InvalidFormatException {
        List<GouZhaoWu> gouZhaoWus = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(this.openFolder(node));
        for (Row row : workbook.getSheetAt(0)) {
            if (dataFormatter.formatCellValue(row.getCell(0)).equals("")) {
                break;
            }
            if (row.getRowNum() != 0) {
                gouZhaoWus.add(buildGouZhaoWu(row));
            }
        }
        workbook.close();
        return gouZhaoWus;
    }

    @Override
    public void exportAggData(List<AggData> aggDataList) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet();

        XSSFRow row = spreadsheet.createRow(0);
        XSSFCell cell0 = row.createCell(0);
        cell0.setCellValue("起点桩号");
        XSSFCell cell1 = row.createCell(1);
        cell1.setCellValue("终点桩号");
        XSSFCell cell2 = row.createCell(2);
        cell2.setCellValue("半径");
        XSSFCell cell3 = row.createCell(3);
        cell3.setCellValue("纵坡");

        int rowNum = 1;
        for(AggData aggData: aggDataList) {
            XSSFRow xssfRow = spreadsheet.createRow(rowNum++);

            xssfRow.createCell(0)
                    .setCellValue(aggData.getStart());

            xssfRow.createCell(1)
                    .setCellValue(aggData.getEnd());

            xssfRow.createCell(2)
                    .setCellValue(aggData.getRadius());

            xssfRow.createCell(3)
                    .setCellValue(aggData.getSlope());

        }

        OutputStream out = new FileOutputStream("agg_data.xlsx");
        workbook.write(out);
        out.close();
        workbook.close();
    }

    private GouZhaoWu buildGouZhaoWu(Row row) {
        return GouZhaoWu.builder()
                .start(Double.valueOf(dataFormatter.formatCellValue(row.getCell(0))))
                .end(Double.valueOf(dataFormatter.formatCellValue(row.getCell(1))))
                .roadStructure(dataFormatter.formatCellValue(row.getCell(2)))
                .build();
    }
}
