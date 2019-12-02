package domain;

import domain.model.*;
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

    public static final Double XiangQian = 200D;
    public static final Double XiangHou = 100D;
    private DataService dataService;

    private List<PingMianXianXing> pingMianXianXings;

    private List<ZongMianXianXing> zongMianXianXings;

    private List<GouZhaoWu> gouZhaoWus;

    private List<HuTongLiJiao> huTongLiJiaos;

    private List<AggData> aggDataList;

    private CarType carType;

    private Double designSpeed;

    private Double startSpeed;

    private Double expectSpeed;

    private Double maxAcceleration;

    private Double minAcceleration;

    public DataHandler() {
        dataService = new FileService();
    }

    public void setUpPingMianXianXingData(VBox node) throws IOException, InvalidFormatException {
        pingMianXianXings = dataService.getPingMianXianXingData(node);
    }

    public void setUpZongMianXianXingData(VBox node) throws IOException, InvalidFormatException {
        zongMianXianXings = dataService.getZongMianXianXingData(node);
    }

    public void setUpGouZhaoWuData(VBox node) throws Exception {
        gouZhaoWus = dataService.getGouZhaoWuData(node);
    }

    public void setUpHuTongLiJiaoData(VBox node) throws Exception {
        huTongLiJiaos = dataService.getHuTongLiJiaoData(node);
    }

    public void exportAggregatingData() throws Exception {
        aggDataList = getAggData();
        dataService.exportAggData(aggDataList);
    }

    private List<AggData> getAggData() throws Exception {
        aggDataList = new ArrayList<>();
        List<Double> totalStakes = getTotalStakes();
        buildAggData(totalStakes);
        mergeSections();
        extensionTunnelStakes();
        addHuTongLiJiaoData();
        analysisData();
        calculateSpeed();
        return aggDataList;
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

    private void buildAggData(List<Double> totalStakes) throws Exception {
        for (int i = 1; i < totalStakes.size(); i++) {
            aggDataList.add(AggData.builder()
                    .start(totalStakes.get(i - 1))
                    .end(totalStakes.get(i))
                    .length(totalStakes.get(i) - totalStakes.get(i - 1))
                    .radius(getMatchedRadius(totalStakes.get(i - 1), totalStakes.get(i)))
                    .slope(getMatchedSlope(totalStakes.get(i - 1), totalStakes.get(i)))
                    .roadStructure(getMatchedRoadStructure(totalStakes.get(i - 1), totalStakes.get(i)))
                    .build());
        }
    }

    private GouZhaoWuType getMatchedRoadStructure(Double start, Double end) throws Exception {
        List<GouZhaoWuType> results = new ArrayList<>();
        for (GouZhaoWu gouZhaoWu : gouZhaoWus) {
            if (gouZhaoWu.getStart() <= start && gouZhaoWu.getEnd() >= start) {
                results.add(gouZhaoWu.getRoadStructure());
            }
            if (gouZhaoWu.getStart() >= start && gouZhaoWu.getEnd() <= end) {
                results.add(gouZhaoWu.getRoadStructure());
            }
            if (gouZhaoWu.getStart() <= end && gouZhaoWu.getEnd() >= end) {
                results.add(gouZhaoWu.getRoadStructure());
            }
        }
        return combineGouZhaoWuResult(results);
    }

    private GouZhaoWuType combineGouZhaoWuResult(List<GouZhaoWuType> results) throws Exception {
        if (results.contains(GouZhaoWuType.QIAO) && results.contains(GouZhaoWuType.LU) && results.contains(GouZhaoWuType.SUI)) {
            return GouZhaoWuType.QIAO_SUI;
        } else if (results.contains(GouZhaoWuType.QIAO) && results.contains(GouZhaoWuType.SUI)) {
            return GouZhaoWuType.QIAO_SUI;
        } else if (results.contains(GouZhaoWuType.LU) && results.contains(GouZhaoWuType.SUI)) {
            return GouZhaoWuType.LU_SUI;
        } else if (results.contains(GouZhaoWuType.QIAO) && results.contains(GouZhaoWuType.LU)) {
            return GouZhaoWuType.LU_QIAO;
        } else if (results.contains(GouZhaoWuType.QIAO)) {
            return GouZhaoWuType.QIAO;
        } else if (results.contains(GouZhaoWuType.LU)) {
            return GouZhaoWuType.LU;
        } else if (results.contains(GouZhaoWuType.SUI)) {
            return GouZhaoWuType.SUI;
        }
        throw new Exception(GouZhaoWuType.ERROR.getValue());
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

    private void mergeSections() {
        aggDataList = aggDataList.stream().filter(it -> it.getLength() >= 100).collect(Collectors.toList());
        for (int i = 1; i < aggDataList.size(); i++) {
            if (!aggDataList.get(i).getStart().equals(aggDataList.get(i - 1).getEnd())) {
                aggDataList.get(i).setStart(aggDataList.get(i - 1).getEnd());
            }
        }
    }

    private void extensionTunnelStakes() {
        Double qian = XiangQian;
        Double hou = XiangHou;
        Double startStake = aggDataList.get(0).getStart();
        Double endStake = aggDataList.get(aggDataList.size() - 1).getEnd();
        for (int i = 0; i < aggDataList.size(); i++) {
            AggData currentAggData = aggDataList.get(i);
            if (currentAggData.getRoadStructure() == GouZhaoWuType.SUI) {
                currentAggData.setStart(getStart(startStake, currentAggData));
                currentAggData.setEnd(getEnd(endStake, currentAggData));

                for (int j = i - 1; j >= 0; j--) {
                    if (aggDataList.get(j).getStart() < currentAggData.getStart()) {
                        aggDataList.get(j).setEnd(aggDataList.get(j).getEnd() - qian);
                        return;
                    } else {
                        qian -= aggDataList.get(j).getLength();
                        aggDataList.remove(j);
                        j++;
                    }
                }

                for (int k = i + 1; k < aggDataList.size(); k++) {
                    if (aggDataList.get(k).getEnd() > currentAggData.getEnd()) {
                        aggDataList.get(k).setStart(aggDataList.get(k).getStart() + hou);
                        return;
                    } else {
                        hou -= aggDataList.get(k).getLength();
                        aggDataList.remove(k);
                        k--;
                    }
                }
            }
        }
    }

    private double getEnd(Double endStake, AggData currentAggData) {
        return currentAggData.getEnd() + XiangHou > endStake ? endStake : currentAggData.getEnd() + XiangHou;
    }

    private double getStart(Double startStake, AggData currentAggData) {
        return currentAggData.getStart() - XiangQian < startStake ? startStake : currentAggData.getStart() - XiangQian;
    }

    private void addHuTongLiJiaoData() {
        for (HuTongLiJiao huTongLiJiao : huTongLiJiaos) {
            for (AggData aggData : aggDataList) {
                if (aggData.getEnd() > huTongLiJiao.getStart() && aggData.getEnd() < huTongLiJiao.getEnd()) {
                    aggData.setHuTongLiJiao(true);
                }
                if (aggData.getStart() > huTongLiJiao.getStart() && aggData.getStart() < huTongLiJiao.getEnd()) {
                    aggData.setHuTongLiJiao(true);
                }
                if (aggData.getStart() < huTongLiJiao.getStart() && aggData.getEnd() > huTongLiJiao.getEnd()) {
                    aggData.setHuTongLiJiao(true);
                }
            }
        }
    }

    private void analysisData() {
        aggDataList.forEach(item -> {
            if (item.getHuTongLiJiao()) {
                item.setRoadType(RoadType.HU_TONG_LI_JIAO_LU_DUAN);
            } else if (item.getRoadStructure() == GouZhaoWuType.SUI ||
                    item.getRoadStructure() == GouZhaoWuType.QIAO_SUI ||
                    item.getRoadStructure() == GouZhaoWuType.LU_SUI) {
                item.setRoadType(RoadType.SUI_DAO_LU_DUAN);
            } else if (item.getRadius() > 1000 &&
                    item.getSlope() < 3 &&
                    item.getLength() > 200) {
                item.setRoadType(RoadType.PING_ZHI_LU_DUAN);
            } else if (item.getRadius() > 1000 &&
                    item.getSlope() < 3 &&
                    item.getLength() < 200) {
                item.setRoadType(RoadType.DUAN_PING_ZHI_LU_DUAN);
            } else if (item.getRadius() > 1000 &&
                    item.getSlope() >= 3) {
                item.setRoadType(RoadType.ZONG_PU_LU_DUAN);
            } else if (item.getRadius() <= 1000 &&
                    item.getSlope() < 3) {
                item.setRoadType(RoadType.PING_QU_LU_DUAN);
            } else if (item.getRadius() <= 1000 &&
                    item.getSlope() >= 3) {
                item.setRoadType(RoadType.WAN_PU_LU_DUAN);
            }
        });
    }

    private void calculateSpeed() {
        setUpStartSpeedTemp();
        Double startSpeedTemp = 0D;
        for (int i = 0; i < aggDataList.size(); i++) {
            if (i == 0) {
                startSpeedTemp = this.startSpeed;
            } else {
                startSpeedTemp = aggDataList.get(i - 1).getEndSpeed();
            }

            if (aggDataList.get(i).getRoadType() == RoadType.DUAN_PING_ZHI_LU_DUAN) {
                aggDataList.get(i).setStartSpeed(startSpeedTemp);
                aggDataList.get(i).setEndSpeed(startSpeedTemp);
            } else if (aggDataList.get(i).getRoadType() == RoadType.PING_ZHI_LU_DUAN) {
                aggDataList.get(i).setStartSpeed(startSpeedTemp);
                aggDataList.get(i).setEndSpeed(calculatePingZhiLuDuan(startSpeedTemp, i));
            }
        }
    }

    private void setUpStartSpeedTemp() {
        if (this.carType == CarType.BIG) {
            if (this.designSpeed == 120) {
                this.startSpeed = 80D;
                this.expectSpeed = 80D;
            } else if (this.designSpeed == 100) {
                this.startSpeed = 75D;
                this.expectSpeed = 80D;
            } else if (this.designSpeed == 80) {
                this.startSpeed = 65D;
                this.expectSpeed = 80D;
            } else if (this.designSpeed == 60) {
                this.startSpeed = 50D;
                this.expectSpeed = 75D;
            }
        } else if (this.carType == CarType.SMALL) {
            if (this.designSpeed == 120) {
                this.startSpeed = 120D;
                this.expectSpeed = 120D;
            } else if (this.designSpeed == 100) {
                this.startSpeed = 100D;
                this.expectSpeed = 120D;
            } else if (this.designSpeed == 80) {
                this.startSpeed = 80D;
                this.expectSpeed = 110D;
            } else if (this.designSpeed == 60) {
                this.startSpeed = 60D;
                this.expectSpeed = 90D;
            }
        }
    }

    private double calculatePingZhiLuDuan(Double startSpeedTemp, int i) {
        return 3.6 * Math.sqrt(
                Math.pow((startSpeedTemp / 3.6), 2) + 2 * aggDataList.get(i).getLength() *
                        minAcceleration + (maxAcceleration - minAcceleration) * (1 - startSpeedTemp / this.expectSpeed)
        );
    }
}
