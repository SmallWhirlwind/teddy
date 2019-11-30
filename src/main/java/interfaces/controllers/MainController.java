package interfaces.controllers;

import domain.DataHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
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
        } catch (IOException | InvalidFormatException e) {
            popAlert(e.getMessage());
        }
    }

    private void popAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.titleProperty().set("ERROR");
        alert.headerTextProperty().set(message);
        alert.showAndWait();
    }
}
