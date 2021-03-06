package interfaces.controllers;

import domain.CarType;
import domain.DataHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;

public class MainController {

    @FXML
    private VBox node;
    @FXML
    private CheckBox p_status;
    @FXML
    public TextField p_direction;
    @FXML
    private CheckBox z_status;
    @FXML
    public TextField z_direction;
    @FXML
    private CheckBox g_status;
    @FXML
    public TextField g_direction;
    @FXML
    private CheckBox h_status;
    @FXML
    public TextField h_direction;
    @FXML
    private LineChart line_chart;
    @FXML
    public MenuItem ds_120;
    @FXML
    public MenuItem ds_100;
    @FXML
    public MenuItem ds_80;
    @FXML
    public MenuItem ds_60;
    @FXML
    public TextField design_speed;
    @FXML
    public TextField car_type;

    private DataHandler dataHandler;

    public MainController() {
        dataHandler = new DataHandler();
    }

    @FXML
    protected void setUpPingMianXianXingData(ActionEvent event) {
        try {
            dataHandler.setUpPingMianXianXingData(node);
            p_direction.setText(dataHandler.isZhengXiangPingMianXianXing() ? "正向" : "反向");
            p_status.setSelected(true);
        } catch (IOException | InvalidFormatException e) {
            popAlert(e.getMessage());
        }
    }

    @FXML
    protected void setUpZongMianXianXingData(ActionEvent event) {
        try {
            dataHandler.setUpZongMianXianXingData(node);
            z_direction.setText(dataHandler.isZhengXiangZongMianXianXing() ? "正向" : "反向");
            z_status.setSelected(true);
        } catch (IOException | InvalidFormatException e) {
            popAlert(e.getMessage());
        }
    }

    @FXML
    protected void setUpGouZhaoWeData(ActionEvent event) {
        try {
            dataHandler.setUpGouZhaoWuData(node);
            g_direction.setText(dataHandler.isZhengXiangGouZhaoWu() ? "正向" : "反向");
            g_status.setSelected(true);
        } catch (Throwable e) {
            popAlert(e.getMessage());
        }
    }

    @FXML
    protected void setUpHuTongLiJiaoData(ActionEvent event) {
        try {
            dataHandler.setUpHuTongLiJiaoData(node);
            h_direction.setText(dataHandler.isZhengXiangHuTongLiJiao() ? "正向" : "反向");
            h_status.setSelected(true);
        } catch (Throwable e) {
            popAlert(e.getMessage());
        }
    }

    @FXML
    protected void aggregatingData(ActionEvent event) {
        try {
            dataHandler.exportAggregatingData(node);
        } catch (Throwable e) {
            popAlert(e.getMessage());
        }
    }

    @FXML
    protected void aggregatingRoadData(ActionEvent event) {
        try {
            dataHandler.exportAggregatingRoadData(node);
        } catch (Throwable e) {
            popAlert(e.getMessage());
        }
    }

    @FXML
    protected void analysisData(ActionEvent event) {
        try {
            dataHandler.exportAnalysisData(node);
        } catch (Throwable e) {
            popAlert(e.getMessage());
        }
    }

    @FXML
    public void showAnalysisData(ActionEvent event) {
        try {
            dataHandler.showAnalysisDataLineChart(line_chart);
        } catch (Throwable e) {
            popAlert(e.getMessage());
        }
    }

    @FXML
    public void analysisSecurityData(ActionEvent event) {
        try {
            dataHandler.exportAnalysisSecurityData(node);
        } catch (Throwable e) {
            popAlert(e.getMessage());
        }
    }

    @FXML
    public void chooseLargeCar(ActionEvent actionEvent) {
        dataHandler.setMaxAcceleration(0.25);
        dataHandler.setMinAcceleration(0.2);
        dataHandler.setMinDriveSpeed(30D);
        dataHandler.setCarType(CarType.BIG);
        car_type.setText("大型车");
    }

    @FXML
    public void chooseSmallCar(ActionEvent actionEvent) {
        dataHandler.setMaxAcceleration(0.5);
        dataHandler.setMinAcceleration(0.15);
        dataHandler.setMinDriveSpeed(50D);
        dataHandler.setCarType(CarType.SMALL);
        car_type.setText("小型车");
    }

    @FXML
    public void chooseDesignSpeed120(ActionEvent actionEvent) {
        dataHandler.setDesignSpeed(120D);
        design_speed.setText("120");
    }

    @FXML
    public void chooseDesignSpeed100(ActionEvent actionEvent) {
        dataHandler.setDesignSpeed(100D);
        design_speed.setText("100");
    }

    @FXML
    public void chooseDesignSpeed80(ActionEvent actionEvent) {
        dataHandler.setDesignSpeed(80D);
        design_speed.setText("80");
    }

    @FXML
    public void chooseDesignSpeed60(ActionEvent actionEvent) {
        dataHandler.setDesignSpeed(60D);
        design_speed.setText("60");
    }

    private void popAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.titleProperty().set("ERROR");
        alert.headerTextProperty().set(message);
        alert.showAndWait();
    }
}
