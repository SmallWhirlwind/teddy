package domain;

import domain.model.PingMianXianXing;
import javafx.scene.layout.VBox;

import java.util.List;

public interface DataService {
    List<PingMianXianXing> getPingMianXianXingData(VBox node);
}
