package interfaces.controllers;

import domain.DataHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    protected void setUpPingMianXianXingData(ActionEvent event) throws IOException, InvalidFormatException {
        dataHandler.setUpPingMianXianXingData(node);
        p_status.setSelected(true);
    }

    @FXML
    protected void setUpZongMianXianXingData(ActionEvent event) throws IOException, InvalidFormatException {
        dataHandler.setUpZongMianXianXingData(node);
        z_status.setSelected(true);
    }

    @FXML
    protected void setUpGouZhaoWeData(ActionEvent event) throws IOException, InvalidFormatException {
        dataHandler.setUpGouZhaoWuData(node);
        g_status.setSelected(true);
    }
}
