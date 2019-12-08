package interfaces.workbook;

import domain.DataService;
import domain.model.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
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

    private File chooseFile(VBox node) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Excel", "*.xls", "*.xlsx")
        );
        return fileChooser.showOpenDialog(node.getScene().getWindow());
    }

    private String chooseFolder(VBox node) throws Exception {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Folder");
        File directory = directoryChooser.showDialog(node.getScene().getWindow());
        if (directory != null) {
            return directory.getAbsolutePath();
        }
        throw new Exception("Folder error");
    }

    @Override
    public List<PingMianXianXing> getPingMianXianXingData(VBox node) throws IOException, InvalidFormatException {
        List<PingMianXianXing> pingMianXianXings = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(this.chooseFile(node));
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
        Workbook workbook = WorkbookFactory.create(this.chooseFile(node));
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
        Workbook workbook = WorkbookFactory.create(this.chooseFile(node));
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
        Workbook workbook = WorkbookFactory.create(this.chooseFile(node));
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
    public void exportAggData(List<AggData> AggDataList, VBox node) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet();

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFDataFormat format = workbook.createDataFormat();
        cellStyle.setDataFormat(format.getFormat("#,##0.0000"));

        XSSFRow row = spreadsheet.createRow(0);
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("起点桩号");
        row.createCell(2).setCellValue("终点桩号");
        row.createCell(3).setCellValue("长度");
        row.createCell(4).setCellValue("半径");
        row.createCell(5).setCellValue("纵坡");
        row.createCell(6).setCellValue("道路构造物");

        int rowNum = 0;
        for (AggData aggData : AggDataList) {
            XSSFRow xssfRow = spreadsheet.createRow(++rowNum);

            xssfRow.createCell(0).setCellValue(rowNum);

            XSSFCell cell_1 = xssfRow.createCell(1);
            cell_1.setCellValue(aggData.getStart());
            cell_1.setCellStyle(cellStyle);

            XSSFCell cell_2 = xssfRow.createCell(2);
            cell_2.setCellStyle(cellStyle);
            cell_2.setCellValue(aggData.getEnd());

            XSSFCell cell_3 = xssfRow.createCell(3);
            cell_3.setCellStyle(cellStyle);
            cell_3.setCellValue(aggData.getLength());

            XSSFCell cell_4 = xssfRow.createCell(4);
            cell_4.setCellStyle(cellStyle);
            cell_4.setCellValue(aggData.getRadius());

            XSSFCell cell_5 = xssfRow.createCell(5);
            cell_5.setCellStyle(cellStyle);
            cell_5.setCellValue(aggData.getSlope());

            XSSFCell cell_6 = xssfRow.createCell(6);
            cell_6.setCellValue(aggData.getRoadStructure().getValue());
        }
        String chooseFolderPath = chooseFolder(node);
        OutputStream out = new FileOutputStream(chooseFolderPath + "/道路结构划分.xlsx");
        workbook.write(out);
        out.close();
        workbook.close();
    }

    @Override
    public void exportRoadAggData(List<AggData> aggDataList, VBox node) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet();

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        // 时间类型格式，如果不设置这个，excel默认展示位number型
        XSSFDataFormat format = workbook.createDataFormat();
        cellStyle.setDataFormat(format.getFormat("#,##0.0000"));

        XSSFRow row = spreadsheet.createRow(0);
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("起点桩号");
        row.createCell(2).setCellValue("终点桩号");
        row.createCell(3).setCellValue("长度");
        row.createCell(4).setCellValue("半径");
        row.createCell(5).setCellValue("纵坡");
        row.createCell(6).setCellValue("道路构造物");
        row.createCell(7).setCellValue("互通立交");
        row.createCell(8).setCellValue("路段类型");

        int rowNum = 0;
        for (AggData aggData : aggDataList) {
            XSSFRow xssfRow = spreadsheet.createRow(++rowNum);

            xssfRow.createCell(0).setCellValue(rowNum);

            XSSFCell cell_1 = xssfRow.createCell(1);
            cell_1.setCellValue(aggData.getStart());
            cell_1.setCellStyle(cellStyle);

            XSSFCell cell_2 = xssfRow.createCell(2);
            cell_2.setCellStyle(cellStyle);
            cell_2.setCellValue(aggData.getEnd());

            XSSFCell cell_3 = xssfRow.createCell(3);
            cell_3.setCellStyle(cellStyle);
            cell_3.setCellValue(aggData.getLength());

            XSSFCell cell_4 = xssfRow.createCell(4);
            cell_4.setCellStyle(cellStyle);
            cell_4.setCellValue(aggData.getRadius());

            XSSFCell cell_5 = xssfRow.createCell(5);
            cell_5.setCellStyle(cellStyle);
            cell_5.setCellValue(aggData.getSlope());

            xssfRow.createCell(6).setCellValue(aggData.getRoadStructure().getValue());

            xssfRow.createCell(7).setCellValue(aggData.getHuTongLiJiaoValue());

            xssfRow.createCell(8).setCellValue(aggData.getRoadType().getValue());
        }

        String chooseFolderPath = chooseFolder(node);
        OutputStream out = new FileOutputStream(chooseFolderPath + "/路段划分.xlsx");
        workbook.write(out);
        out.close();
        workbook.close();

    }

    @Override
    public void exportAnalysisAggData(List<AggData> analysisDataList, VBox node) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet();

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        // 时间类型格式，如果不设置这个，excel默认展示位number型
        XSSFDataFormat format = workbook.createDataFormat();
        cellStyle.setDataFormat(format.getFormat("#,##0.0000"));

        XSSFRow row = spreadsheet.createRow(0);
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("起点桩号");
        row.createCell(2).setCellValue("终点桩号");
        row.createCell(3).setCellValue("长度");
        row.createCell(4).setCellValue("半径");
        row.createCell(5).setCellValue("纵坡");
        row.createCell(6).setCellValue("道路构造物");
        row.createCell(7).setCellValue("互通立交");
        row.createCell(8).setCellValue("路段类型");
        row.createCell(9).setCellValue("入口速度");
        row.createCell(10).setCellValue("中点速度");
        row.createCell(11).setCellValue("出口速度");

        int rowNum = 0;
        for (AggData aggData : analysisDataList) {
            XSSFRow xssfRow = spreadsheet.createRow(++rowNum);

            xssfRow.createCell(0).setCellValue(rowNum);

            XSSFCell cell_1 = xssfRow.createCell(1);
            cell_1.setCellValue(aggData.getStart());
            cell_1.setCellStyle(cellStyle);

            XSSFCell cell_2 = xssfRow.createCell(2);
            cell_2.setCellStyle(cellStyle);
            cell_2.setCellValue(aggData.getEnd());

            XSSFCell cell_3 = xssfRow.createCell(3);
            cell_3.setCellStyle(cellStyle);
            cell_3.setCellValue(aggData.getLength());

            XSSFCell cell_4 = xssfRow.createCell(4);
            cell_4.setCellStyle(cellStyle);
            cell_4.setCellValue(aggData.getRadius());

            XSSFCell cell_5 = xssfRow.createCell(5);
            cell_5.setCellStyle(cellStyle);
            cell_5.setCellValue(aggData.getSlope());

            xssfRow.createCell(6).setCellValue(aggData.getRoadStructure().getValue());

            xssfRow.createCell(7).setCellValue(aggData.getHuTongLiJiaoValue());

            xssfRow.createCell(8).setCellValue(aggData.getRoadType().getValue());

            xssfRow.createCell(9).setCellValue(aggData.getStartSpeed());

            xssfRow.createCell(10).setCellValue(aggData.getMiddleSpeed());

            xssfRow.createCell(11).setCellValue(aggData.getEndSpeed());
        }

        String chooseFolderPath = chooseFolder(node);
        OutputStream out = new FileOutputStream(chooseFolderPath + "/速度预测.xlsx");
        workbook.write(out);
        out.close();
        workbook.close();
    }
}
