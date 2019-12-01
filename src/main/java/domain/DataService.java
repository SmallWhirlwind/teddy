package domain;

import domain.model.AggData;
import domain.model.GouZhaoWu;
import domain.model.PingMianXianXing;
import domain.model.ZongMianXianXing;
import javafx.scene.layout.VBox;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.List;

public interface DataService {
    List<PingMianXianXing> getPingMianXianXingData(VBox node) throws IOException, InvalidFormatException;

    List<ZongMianXianXing> getZongMianXianXingData(VBox node) throws IOException, InvalidFormatException;

    List<GouZhaoWu> getGouZhaoWuData(VBox node) throws Exception;

    void exportAggData(List<AggData> aggDataList) throws IOException;
}
