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

    private Double minDriveSpeed;

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
        calculateHuTongShiLiTiJiaoCha();
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
        Double startSpeedTemp;
        for (int i = 0; i < aggDataList.size(); i++) {
            if (i == 0) {
                startSpeedTemp = this.startSpeed;
            } else {
                startSpeedTemp = aggDataList.get(i - 1).getEndSpeed();
            }

            if (aggDataList.get(i).getRoadType() == RoadType.DUAN_PING_ZHI_LU_DUAN) {
                aggDataList.get(i).setStartSpeed(startSpeedTemp);
                aggDataList.get(i).setMiddleSpeed(startSpeedTemp);
                aggDataList.get(i).setEndSpeed(startSpeedTemp);
            } else if (aggDataList.get(i).getRoadType() == RoadType.PING_ZHI_LU_DUAN) {
                aggDataList.get(i).setStartSpeed(startSpeedTemp);
                aggDataList.get(i).setMiddleSpeed(calculateMiddlePingZhiLuDuan(i));
                aggDataList.get(i).setEndSpeed(calculateEndPingZhiLuDuan(i));
            } else if (aggDataList.get(i).getRoadType() == RoadType.PING_QU_LU_DUAN) {
                aggDataList.get(i).setStartSpeed(startSpeedTemp);
                aggDataList.get(i).setMiddleSpeed(calculateMiddlePingQuLuDuan(i));
                aggDataList.get(i).setEndSpeed(calculateEndPingQuLuDuan(i));
            } else if (aggDataList.get(i).getRoadType() == RoadType.ZONG_PU_LU_DUAN) {
                aggDataList.get(i).setStartSpeed(startSpeedTemp);
                aggDataList.get(i).setMiddleSpeed(calculateMiddleZongPuLuDuan(i));
                aggDataList.get(i).setEndSpeed(calculateEndZongPuLuDuan(i));
            } else if (aggDataList.get(i).getRoadType() == RoadType.WAN_PU_LU_DUAN) {
                aggDataList.get(i).setStartSpeed(startSpeedTemp);
                aggDataList.get(i).setMiddleSpeed(calculateMiddleWanPuLuDuan(i));
                aggDataList.get(i).setEndSpeed(calculateEndWanPuLuDuan(i));
            } else if (aggDataList.get(i).getRoadType() == RoadType.SUI_DAO_LU_DUAN) {
                aggDataList.get(i).setStartSpeed(startSpeed);
                aggDataList.get(i).setMiddleSpeed(calculateMiddleSuiDaoLuDuan(i));
                aggDataList.get(i).setEndSpeed(calculateEndSuiDaoLuDuan(i));
            }
        }
    }

    private void calculateHuTongShiLiTiJiaoCha() {
        for (int i = 0; i < aggDataList.size(); i++) {
            if (aggDataList.get(i).getHuTongLiJiao()) {
                if (this.carType == CarType.SMALL) {
                    if (i != 0) {
                        aggDataList.get(i).setStartSpeed(aggDataList.get(i).getStartSpeed() - 8);
                    }
                    aggDataList.get(i).setMiddleSpeed(aggDataList.get(i).getMiddleSpeed() - 8);
                    aggDataList.get(i).setEndSpeed(aggDataList.get(i).getEndSpeed() - 8);
                } else {
                    if (i != 0) {
                        aggDataList.get(i).setStartSpeed(aggDataList.get(i).getStartSpeed() - 5);
                    }
                    aggDataList.get(i).setMiddleSpeed(aggDataList.get(i).getMiddleSpeed() - 5);
                    aggDataList.get(i).setEndSpeed(aggDataList.get(i).getEndSpeed() - 5);
                }
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

    private double calculateEndPingZhiLuDuan(int i) {
        AggData currentAggData = aggDataList.get(i);
        double calculateSpeed = 3.6 * Math.sqrt(
                Math.pow((currentAggData.getStartSpeed() / 3.6), 2) + 2 * currentAggData.getLength() *
                        minAcceleration + (maxAcceleration - minAcceleration) * (1 - currentAggData.getStartSpeed() / this.expectSpeed)
        );
        return compareAndGetSmall(calculateSpeed, this.expectSpeed);
    }

    private Double calculateMiddlePingZhiLuDuan(int i) {
        AggData currentAggData = aggDataList.get(i);
        double calculateSpeed = 3.6 * Math.sqrt(
                Math.pow((currentAggData.getStartSpeed() / 3.6), 2) + 2 * (currentAggData.getLength() / 2) *
                        minAcceleration + (maxAcceleration - minAcceleration) * (1 - currentAggData.getStartSpeed() / this.expectSpeed)
        );
        return compareAndGetSmall(calculateSpeed, this.expectSpeed);
    }

    private Double calculateMiddlePingQuLuDuan(int i) {
        AggData currentAggData = aggDataList.get(i);
        if (i == 0 || aggDataList.get(i - 1).getRadius() > 1000) {
            if (this.carType == CarType.SMALL) {
                return -24.212 + 0.834 * currentAggData.getStartSpeed() + 5.729 * Math.log(currentAggData.getRadius());
            } else {
                return -9.432 + 0.963 * currentAggData.getStartSpeed() + 1.522 * Math.log(currentAggData.getRadius());
            }
        } else {
            if (this.carType == CarType.SMALL) {
                return 1.277 + 0.942 * currentAggData.getStartSpeed() + 6.19 * Math.log(currentAggData.getRadius()) - 5.959 * Math.log(aggDataList.get(i - 1).getRadius());
            } else {
                return -24.472 + 0.990 * currentAggData.getStartSpeed() + 3.629 * Math.log(currentAggData.getRadius());
            }
        }
    }

    private Double calculateEndPingQuLuDuan(int i) {
        AggData currentAggData = aggDataList.get(i);
        if (i == aggDataList.size() - 1 || aggDataList.get(i - 1).getRadius() > 1000) {
            if (this.carType == CarType.SMALL) {
                return 11.946 + 0.908 * currentAggData.getMiddleSpeed();
            } else {
                return 5.217 + 0.926 * currentAggData.getMiddleSpeed();
            }
        } else {
            if (this.carType == CarType.SMALL) {
                return -11.299 + 0.936 * currentAggData.getMiddleSpeed() - 2.060 * Math.log(currentAggData.getRadius()) + 5.203 * Math.log(aggDataList.get(i - 1).getRadius());
            } else {
                return 5.899 + 0.925 * currentAggData.getMiddleSpeed() - 1.005 * Math.log(currentAggData.getRadius()) + 0.329 * Math.log(aggDataList.get(i - 1).getRadius());
            }
        }
    }

    private Double calculateMiddleZongPuLuDuan(int i) {
        AggData currentAggData = aggDataList.get(i);
        if (currentAggData.getSlope() > 4) {
            if (this.carType == CarType.SMALL) {
                double calculateSpeed = currentAggData.getStartSpeed() - 8 * (Math.floor(currentAggData.getLength() / 1000 / 2));
                return compareAndGetBig(calculateSpeed, this.minDriveSpeed);
            } else {
                double calculateSpeed = currentAggData.getStartSpeed() - 20 * (Math.floor(currentAggData.getLength() / 1000 / 2));
                return compareAndGetBig(calculateSpeed, this.minDriveSpeed);
            }
        } else if (currentAggData.getSlope() <= 4 && currentAggData.getSlope() >= 3) {
            if (this.carType == CarType.SMALL) {
                double calculateSpeed = currentAggData.getStartSpeed() - 5 * (Math.floor(currentAggData.getLength() / 1000 / 2));
                return compareAndGetBig(calculateSpeed, this.minDriveSpeed);
            } else {
                double calculateSpeed = currentAggData.getStartSpeed() - 10 * (Math.floor(currentAggData.getLength() / 1000 / 2));
                return compareAndGetBig(calculateSpeed, this.minDriveSpeed);
            }
        } else if (currentAggData.getSlope() < -4) {
            if (this.carType == CarType.SMALL) {
                double calculateSpeed = currentAggData.getStartSpeed() + 20 * (Math.floor(currentAggData.getLength() / 500 / 2));
                return compareAndGetSmall(calculateSpeed, this.expectSpeed);
            } else {
                double calculateSpeed = currentAggData.getStartSpeed() + 15 * (Math.floor(currentAggData.getLength() / 500 / 2));
                return compareAndGetSmall(calculateSpeed, this.expectSpeed);
            }
        } else if (currentAggData.getSlope() >= -4 && currentAggData.getSlope() <= -3) {
            if (this.carType == CarType.SMALL) {
                double calculateSpeed = currentAggData.getStartSpeed() + 10 * (Math.floor(currentAggData.getLength() / 500 / 2));
                return compareAndGetSmall(calculateSpeed, this.expectSpeed);
            } else {
                double calculateSpeed = currentAggData.getStartSpeed() + 7.5 * (Math.floor(currentAggData.getLength() / 500 / 2));
                return compareAndGetSmall(calculateSpeed, this.expectSpeed);
            }
        }
        return null;
    }

    private Double calculateEndZongPuLuDuan(int i) {
        AggData currentAggData = aggDataList.get(i);
        if (currentAggData.getSlope() > 4) {
            if (this.carType == CarType.SMALL) {
                double calculateSpeed = currentAggData.getStartSpeed() - 8 * (Math.floor(currentAggData.getLength() / 1000));
                return compareAndGetBig(calculateSpeed, this.minDriveSpeed);
            } else {
                double calculateSpeed = currentAggData.getStartSpeed() - 20 * (Math.floor(currentAggData.getLength() / 1000));
                return compareAndGetBig(calculateSpeed, this.minDriveSpeed);
            }
        } else if (currentAggData.getSlope() <= 4 && currentAggData.getSlope() >= 3) {
            if (this.carType == CarType.SMALL) {
                double calculateSpeed = currentAggData.getStartSpeed() - 5 * (Math.floor(currentAggData.getLength() / 1000));
                return compareAndGetBig(calculateSpeed, this.minDriveSpeed);
            } else {
                double calculateSpeed = currentAggData.getStartSpeed() - 10 * (Math.floor(currentAggData.getLength() / 1000));
                return compareAndGetBig(calculateSpeed, this.minDriveSpeed);
            }
        } else if (currentAggData.getSlope() < -4) {
            if (this.carType == CarType.SMALL) {
                double calculateSpeed = currentAggData.getStartSpeed() + 20 * (Math.floor(currentAggData.getLength() / 500));
                return compareAndGetSmall(calculateSpeed, this.expectSpeed);
            } else {
                double calculateSpeed = currentAggData.getStartSpeed() + 15 * (Math.floor(currentAggData.getLength() / 500));
                return compareAndGetSmall(calculateSpeed, this.expectSpeed);
            }
        } else if (currentAggData.getSlope() >= -4 && currentAggData.getSlope() <= -3) {
            if (this.carType == CarType.SMALL) {
                double calculateSpeed = currentAggData.getStartSpeed() + 10 * (Math.floor(currentAggData.getLength() / 500));
                return compareAndGetSmall(calculateSpeed, this.expectSpeed);
            } else {
                double calculateSpeed = currentAggData.getStartSpeed() + 7.5 * (Math.floor(currentAggData.getLength() / 500));
                return compareAndGetSmall(calculateSpeed, this.expectSpeed);
            }
        }
        return null;
    }

    private Double calculateMiddleWanPuLuDuan(int i) {
        AggData currentAggData = aggDataList.get(i);
        AggData preAggData = i == 0 ? aggDataList.get(i) : aggDataList.get(i - 1);
        if (i == 0 || preAggData.getRadius() > 1000) {
            if (this.carType == CarType.SMALL) {
                return -31.67 + 0.547 * currentAggData.getStartSpeed() + 11.71 * Math.log(currentAggData.getRadius()) - 0.176 * preAggData.getSlope();
            } else {
                return 1.782 + 0.859 * currentAggData.getStartSpeed() - 0.51 * preAggData.getSlope() + 1.196 * Math.log(currentAggData.getRadius());
            }
        } else {
            if (this.carType == CarType.SMALL) {
                return 0.750 + 0.802 * currentAggData.getStartSpeed() + 2.717 * Math.log(currentAggData.getRadius()) - 0.281 * Math.log(preAggData.getSlope());
            } else {
                return 1.798 + 0.248 * Math.log(currentAggData.getRadius()) + 0.977 * currentAggData.getStartSpeed() - 0.133 * preAggData.getSlope() + 0.23 * Math.log(preAggData.getRadius());
            }
        }
    }

    private Double calculateEndWanPuLuDuan(int i) {
        AggData currentAggData = aggDataList.get(i);
        AggData preAggData = i == 0 ? aggDataList.get(i) : aggDataList.get(i - 1);
        AggData postAggData = i == aggDataList.size() - 1 ? aggDataList.get(i) : aggDataList.get(i + 1);
        if (i == aggDataList.size() - 1 || preAggData.getRadius() > 1000) {
            if (this.carType == CarType.SMALL) {
                return 27.294 + 0.720 * currentAggData.getMiddleSpeed() - 1.444 * postAggData.getSlope();
            } else {
                return 13.490 + 0.797 * currentAggData.getMiddleSpeed() - 0.6971 * postAggData.getSlope();
            }
        } else {
            if (this.carType == CarType.SMALL) {
                return 1.819 + 0.839 * currentAggData.getMiddleSpeed() + 1.427 * Math.log(currentAggData.getRadius()) + 0.782 * Math.log(postAggData.getRadius()) - 0.48 * postAggData.getSlope();
            } else {
                return 26.837 + 0.109 * Math.log(postAggData.getRadius()) - 3.039 * Math.log(currentAggData.getRadius()) - 0.594 * postAggData.getSlope() + 0.830 * currentAggData.getMiddleSpeed();
            }
        }
    }

    private Double calculateMiddleSuiDaoLuDuan(int i) {
        AggData currentAggData = aggDataList.get(i);
        if (this.carType == CarType.SMALL) {
            return 0.81 * currentAggData.getStartSpeed() + 8.22;
        } else {
            return 0.85 * currentAggData.getStartSpeed() + 3.89;
        }
    }

    private Double calculateEndSuiDaoLuDuan(int i) {
        AggData currentAggData = aggDataList.get(i);
        if (this.carType == CarType.SMALL) {
            return 0.74 * currentAggData.getStartSpeed() + 16.43;
        } else {
            return 0.45 * currentAggData.getStartSpeed() + 42.61;
        }
    }

    private Double compareAndGetBig(Double a, Double b) {
        return a > b ? a : b;
    }

    private Double compareAndGetSmall(Double a, Double b) {
        return a < b ? a : b;
    }
}
