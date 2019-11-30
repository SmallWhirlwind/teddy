package domain;

import domain.model.AggData;
import domain.model.GouZhaoWu;
import domain.model.PingMianXianXing;
import domain.model.ZongMianXianXing;
import interfaces.workbook.FileService;
import javafx.scene.layout.VBox;
import lombok.Data;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class DataHandler {

    private DataService dataService;

    private List<PingMianXianXing> pingMianXianXings;

    private List<ZongMianXianXing> zongMianXianXings;

    private List<GouZhaoWu> gouZhaoWus;

    public DataHandler() {
        dataService = new FileService();
    }

    public void setUpPingMianXianXingData(VBox node) throws IOException, InvalidFormatException {
        pingMianXianXings = dataService.getPingMianXianXingData(node);
    }

    public void setUpZongMianXianXingData(VBox node) throws IOException, InvalidFormatException {
        zongMianXianXings = dataService.getZongMianXianXingData(node);
    }

    public void setUpGouZhaoWuData(VBox node) throws IOException, InvalidFormatException {
        gouZhaoWus = dataService.getGouZhaoWuData(node);
    }

    public void exportAggregatingData() throws IOException {
        List<AggData> aggDataList = getAggData();
        dataService.exportAggData(aggDataList);
    }

    private List<AggData> getAggData() {
        List<AggData> aggDataList = new ArrayList<>();
        List<Double> totalStakes = getTotalStakes();
        for (int i = 1; i < totalStakes.size(); i++) {
            aggDataList.add(AggData.builder()
                    .start(totalStakes.get(i - 1))
                    .end(totalStakes.get(i))
                    .length(totalStakes.get(i) - totalStakes.get(i - 1))
                    .radius(getMatchedRadius(totalStakes.get(i - 1), totalStakes.get(i)))
                    .slope(getMatchedSlope(totalStakes.get(i - 1), totalStakes.get(i)))
                    .build());
        }
        return aggDataList;
    }

    private Double getMatchedRadius(Double start, Double end) {
        for (PingMianXianXing pingMianXianXing : pingMianXianXings) {
            if (pingMianXianXing.getStart() <= start && pingMianXianXing.getEnd() >= end) {
                return pingMianXianXing.getRadius();
            }
        }
        return 0D;
    }

    private Double getMatchedSlope(Double start, Double end) {
        for (ZongMianXianXing zongMianXianXing : zongMianXianXings) {
            if (zongMianXianXing.getStart() <= start && zongMianXianXing.getEnd() >= end) {
                return zongMianXianXing.getSlope();
            }
        }
        return 0D;
    }

    private List<Double> getTotalStakes() {
        List<Double> startP = pingMianXianXings.stream().map(PingMianXianXing::getStart).collect(Collectors.toList());
        List<Double> startZ = zongMianXianXings.stream().map(ZongMianXianXing::getStart).collect(Collectors.toList());
        List<Double> endP = pingMianXianXings.stream().map(PingMianXianXing::getEnd).collect(Collectors.toList());
        List<Double> endZ = zongMianXianXings.stream().map(ZongMianXianXing::getEnd).collect(Collectors.toList());

        Stream<Double> startStream = Stream.concat(startP.stream(), startZ.stream()).distinct();
        Stream<Double> endStream = Stream.concat(endP.stream(), endZ.stream()).distinct();
        return Stream.concat(startStream, endStream).distinct().sorted().collect(Collectors.toList());
    }
}
