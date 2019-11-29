package interfaces.controllers;

import domain.DataHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML
    private VBox node;

    private DataHandler dataHandler;

    public MainController() {
        dataHandler = new DataHandler();
    }

    @FXML
    protected void setUpPingMianXianXingData(ActionEvent event) {
        dataHandler.setUpPingMianXianXingData(node);
    }

    @FXML
    protected void setUpZongMianXianXingData(ActionEvent event) {
        dataHandler.setUpZongMianXianXingData(node);
    }

    @FXML
    protected void setUpGouZhaoWeData(ActionEvent event) {
        dataHandler.setUpGouZhaoWuData(node);
    }
}
