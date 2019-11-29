package domain;

import domain.model.GouZhaoWu;
import domain.model.PingMianXianXing;
import domain.model.ZongMianXianXing;
import javafx.scene.layout.VBox;

import java.util.List;

public interface DataService {
    List<PingMianXianXing> getPingMianXianXingData(VBox node);

    List<ZongMianXianXing> getZongMianXianXingData(VBox node);

    List<GouZhaoWu> getGouZhaoWuData(VBox node);
}
