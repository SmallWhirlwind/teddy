package domain;

import domain.model.GouZhaoWu;
import domain.model.PingMianXianXing;
import domain.model.ZongMianXianXing;
import interfaces.workbook.FileService;
import javafx.scene.layout.VBox;
import lombok.Data;

import java.util.List;

@Data
public class DataHandler {

    private DataService dataService;

    private List<PingMianXianXing> pingMianXianXings;

    private List<ZongMianXianXing> zongMianXianXings;

    private List<GouZhaoWu> gouZhaoWus;

    public DataHandler() {
        dataService = new FileService();
    }

    public void setUpPingMianXianXingData(VBox node) {
        pingMianXianXings = dataService.getPingMianXianXingData(node);
    }

    public void setUpZongMianXianXingData(VBox node) {
        zongMianXianXings = dataService.getZongMianXianXingData(node);
    }

    public void setUpGouZhaoWuData(VBox node) {
        gouZhaoWus = dataService.getGouZhaoWuData(node);
    }
}
