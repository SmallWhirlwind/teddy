package interfaces.controllers;

import domain.CarType;
import domain.DataHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    private CheckBox agg_status;
    @FXML
    public MenuItem s_120;
    @FXML
    public MenuItem s_100;
    @FXML
    public MenuItem s_80;
    @FXML
    public MenuItem s_75;
    @FXML
    public MenuItem s_65;
    @FXML
    public MenuItem s_60;
    @FXML
    public MenuItem s_50;

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
            dataHandler.exportAggregatingData();
            agg_status.setSelected(true);
        } catch (Exception e) {
            popAlert(e.getMessage());
        }
    }

    @FXML
    public void chooseLargeCar(ActionEvent actionEvent) {
        s_120.setVisible(false);
        s_100.setVisible(false);
        s_80.setVisible(true);
        s_75.setVisible(true);
        s_65.setVisible(true);
        s_60.setVisible(false);
        s_50.setVisible(true);
        dataHandler.setCarType(CarType.BIG);
    }

    @FXML
    public void chooseSmallCar(ActionEvent actionEvent) {
        s_120.setVisible(true);
        s_100.setVisible(true);
        s_80.setVisible(true);
        s_75.setVisible(false);
        s_65.setVisible(false);
        s_60.setVisible(true);
        s_50.setVisible(false);
        dataHandler.setCarType(CarType.SMALL);
    }

    @FXML
    public void chooseSpeed120(ActionEvent actionEvent) {
        dataHandler.setStartSpeed(120D);
    }
    @FXML
    public void chooseSpeed100(ActionEvent actionEvent) {
        dataHandler.setStartSpeed(100D);
    }
    @FXML
    public void chooseSpeed80(ActionEvent actionEvent) {
        dataHandler.setStartSpeed(80D);
    }

    @FXML
    public void chooseSpeed75(ActionEvent actionEvent) {
        dataHandler.setStartSpeed(75D);
    }

    @FXML
    public void chooseSpeed65(ActionEvent actionEvent) {
        dataHandler.setStartSpeed(65D);
    }
    @FXML
    public void chooseSpeed60(ActionEvent actionEvent) {
        dataHandler.setStartSpeed(60D);
    }

    @FXML
    public void chooseSpeed50(ActionEvent actionEvent) {
        dataHandler.setStartSpeed(50D);
    }

    private void popAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.titleProperty().set("ERROR");
        alert.headerTextProperty().set(message);
        alert.showAndWait();
    }
}
