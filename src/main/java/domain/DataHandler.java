package domain;

import domain.model.PingMianXianXing;
import interfaces.workbook.FileService;
import javafx.scene.layout.VBox;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataHandler {

    private DataService dataService;

    private List<PingMianXianXing> pingMianXianXings;

    public DataHandler() {
        dataService = new FileService();
        this.pingMianXianXings = new ArrayList<>();
    }

    public void setUpPingMianXianXingData(VBox node) {
        pingMianXianXings = dataService.getPingMianXianXingData(node);
    }
}
