package interfaces.workbook;

import domain.DataService;
import domain.model.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.*;

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
    public List<GouZhaoWu> getGouZhaoWuData(VBox node) throws Exception {
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

    private GouZhaoWu buildGouZhaoWu(Row row) throws Exception {
        return GouZhaoWu.builder()
                .start(Double.valueOf(dataFormatter.formatCellValue(row.getCell(0))))
                .end(Double.valueOf(dataFormatter.formatCellValue(row.getCell(1))))
                .roadStructure(GouZhaoWuType.getType(dataFormatter.formatCellValue(row.getCell(2))))
                .build();
    }

    @Override
    public List<HuTongLiJiao> getHuTongLiJiaoData(VBox node) throws Exception {
        List<HuTongLiJiao> huTongLiJiaos = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(this.openFolder(node));
        for (Row row : workbook.getSheetAt(0)) {
            if (dataFormatter.formatCellValue(row.getCell(0)).equals("")) {
                break;
            }
            if (row.getRowNum() != 0) {
                huTongLiJiaos.add(buildHuTongLiJiao(row));
            }
        }
        workbook.close();
        return huTongLiJiaos;
    }

    private HuTongLiJiao buildHuTongLiJiao(Row row) throws Exception {
        return HuTongLiJiao.builder()
                .start(Double.valueOf(dataFormatter.formatCellValue(row.getCell(0))))
                .end(Double.valueOf(dataFormatter.formatCellValue(row.getCell(1))))
                .build();
    }

    @Override
    public void exportAggData(List<AggData> aggDataList) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet();

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        // 时间类型格式，如果不设置这个，excel默认展示位number型
        XSSFDataFormat format = workbook.createDataFormat();
        cellStyle.setDataFormat(format.getFormat("#,##0.0000"));

        XSSFRow row = spreadsheet.createRow(0);
        XSSFCell cell0 = row.createCell(0);
        cell0.setCellValue("起点桩号");
        XSSFCell cell1 = row.createCell(1);
        cell1.setCellValue("终点桩号");
        XSSFCell cell2 = row.createCell(2);
        cell2.setCellValue("长度");
        XSSFCell cell3 = row.createCell(3);
        cell3.setCellValue("半径");
        XSSFCell cell4 = row.createCell(4);
        cell4.setCellValue("纵坡");
        XSSFCell cell5 = row.createCell(5);
        cell5.setCellValue("道路构造物");
        XSSFCell cell6 = row.createCell(6);
        cell6.setCellValue("是否为互通立交");
        XSSFCell cell7 = row.createCell(7);
        cell7.setCellValue("路段分析");

        int rowNum = 1;
        for (AggData aggData : aggDataList) {
            XSSFRow xssfRow = spreadsheet.createRow(rowNum++);

            XSSFCell cell_0 = xssfRow.createCell(0);
            cell_0.setCellValue(aggData.getStart());
            cell_0.setCellStyle(cellStyle);

            XSSFCell cell_1 = xssfRow.createCell(1);
            cell_1.setCellStyle(cellStyle);
            cell_1.setCellValue(aggData.getEnd());

            XSSFCell cell_2 = xssfRow.createCell(2);
            cell_2.setCellStyle(cellStyle);
            cell_2.setCellValue(aggData.getLength());

            XSSFCell cell_3 = xssfRow.createCell(3);
            cell_3.setCellStyle(cellStyle);
            cell_3.setCellValue(aggData.getRadius());

            XSSFCell cell_4 = xssfRow.createCell(4);
            cell_4.setCellStyle(cellStyle);
            cell_4.setCellValue(aggData.getSlope());

            XSSFCell cell_5 = xssfRow.createCell(5);
            cell_5.setCellValue(aggData.getRoadStructure().getValue());

            XSSFCell cell_6 = xssfRow.createCell(6);
            cell_6.setCellValue(aggData.getHuTongLiJiao().toString());

            XSSFCell cell_7 = xssfRow.createCell(7);
            cell_7.setCellValue(aggData.getRoadType().getValue());
        }

        OutputStream out = new FileOutputStream(System.getProperty("user.home") + "/agg_data.xlsx");
        workbook.write(out);
        out.close();
        workbook.close();
    }
}
