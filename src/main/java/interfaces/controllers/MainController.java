package interfaces.controllers;

import domain.DataHandler;
import domain.model.PingMianXianXing;
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
    protected void handleButtonAction(ActionEvent event) {
        dataHandler.setUpPingMianXianXingData(node);
    }

}
