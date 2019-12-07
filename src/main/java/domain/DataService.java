package domain;

import domain.model.*;
import javafx.scene.layout.VBox;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.List;

public interface DataService {
    List<PingMianXianXing> getPingMianXianXingData(VBox node) throws IOException, InvalidFormatException;

    List<ZongMianXianXing> getZongMianXianXingData(VBox node) throws IOException, InvalidFormatException;

    List<GouZhaoWu> getGouZhaoWuData(VBox node) throws Exception;

    void exportAggData(List<AggData> aggDataList) throws IOException;

    void exportAnalysisAggData(List<AggData> analysisDataList) throws IOException;

    List<HuTongLiJiao> getHuTongLiJiaoData(VBox node) throws Exception;
}
