package interfaces.controllers;

import domain.CarType;
import domain.DataHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;

public class MainController {

    @FXML
    private VBox node;
    @FXML
    private CheckBox p_status;
    @FXML
    private CheckBox z_status;
    @FXML
    private CheckBox g_status;
    @FXML
    private CheckBox h_status;
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

    private DataHandler dataHandler;

    public MainController() {
        dataHandler = new DataHandler();
    }

    @FXML
    protected void setUpPingMianXianXingData(ActionEvent event) {
        try {
            dataHandler.setUpPingMianXianXingData(node);
            p_status.setSelected(true);
        } catch (IOException | InvalidFormatException e) {
            popAlert(e.getMessage());
        }
    }

    @FXML
    protected void setUpZongMianXianXingData(ActionEvent event) {
        try {
            dataHandler.setUpZongMianXianXingData(node);
            z_status.setSelected(true);
        } catch (IOException | InvalidFormatException e) {
            popAlert(e.getMessage());
        }
    }

    @FXML
    protected void setUpGouZhaoWeData(ActionEvent event) {
        try {
            dataHandler.setUpGouZhaoWuData(node);
            g_status.setSelected(true);
        } catch (Throwable e) {
            popAlert(e.getMessage());
        }
    }

    @FXML
    protected void setUpHuTongLiJiaoData(ActionEvent event) {
        try {
            dataHandler.setUpHuTongLiJiaoData(node);
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
    public void chooseLargeCar(ActionEvent actionEvent) {
        dataHandler.setMaxAcceleration(0.25);
        dataHandler.setMinAcceleration(0.2);
        dataHandler.setMinDriveSpeed(30D);
        dataHandler.setCarType(CarType.BIG);
    }

    @FXML
    public void chooseSmallCar(ActionEvent actionEvent) {
        dataHandler.setMaxAcceleration(0.5);
        dataHandler.setMinAcceleration(0.15);
        dataHandler.setMinDriveSpeed(50D);
        dataHandler.setCarType(CarType.SMALL);
    }

    @FXML
    public void chooseDesignSpeed120(ActionEvent actionEvent) {
        dataHandler.setDesignSpeed(120D);
    }

    @FXML
    public void chooseDesignSpeed100(ActionEvent actionEvent) {
        dataHandler.setDesignSpeed(100D);
    }

    @FXML
    public void chooseDesignSpeed80(ActionEvent actionEvent) {
        dataHandler.setDesignSpeed(80D);
    }

    @FXML
    public void chooseDesignSpeed60(ActionEvent actionEvent) {
        dataHandler.setDesignSpeed(60D);
    }

    private void popAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.titleProperty().set("ERROR");
        alert.headerTextProperty().set(message);
        alert.showAndWait();
    }
}
