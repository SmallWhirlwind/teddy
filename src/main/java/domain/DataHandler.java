package domain;

import domain.model.*;
import interfaces.workbook.FileService;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
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

    private List<GouZhaoWu> gouZhaoWus = new ArrayList<>();

    private List<GouZhaoWu> suiDaoGouZhaoWus = new ArrayList<>();

    private List<HuTongLiJiao> huTongLiJiaos;

    private List<AggData> aggDataList = new ArrayList<>();

    private List<AggData> analysisDataList = new ArrayList<>();

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
        suiDaoGouZhaoWus = gouZhaoWus.stream().map(GouZhaoWu::clone).collect(Collectors.toList());
        extensionTunnelStakes();
    }

    public void setUpHuTongLiJiaoData(VBox node) throws Exception {
        huTongLiJiaos = dataService.getHuTongLiJiaoData(node);
    }

    public void exportAggregatingData(VBox node) throws Exception {
        getAggData();
        dataService.exportAggData(aggDataList, node);
    }

    public void exportAggregatingRoadData(VBox node) throws Exception {
        getAnalysisData();
        dataService.exportRoadAggData(analysisDataList, node);
    }

    public void exportAnalysisData(VBox node) throws Exception {
        getAnalysisData();
        dataService.exportAnalysisAggData(analysisDataList, node);
    }

    public void showAnalysisDataLineChart(LineChart line_chart) throws Exception {
        getAnalysisData();
        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
        for (int i = 0; i < analysisDataList.size(); i++) {
            if (i == 0) {
                series.getData().add(new XYChart.Data<Number, Number>(analysisDataList.get(i).getStart(), analysisDataList.get(i).getStartSpeed()));
            }
            series.getData().add(new XYChart.Data<Number, Number>((analysisDataList.get(i).getEnd() + analysisDataList.get(i).getStart()) / 2, analysisDataList.get(i).getMiddleSpeed()));
            series.getData().add(new XYChart.Data<Number, Number>(analysisDataList.get(i).getEnd(), analysisDataList.get(i).getEndSpeed()));
        }
        line_chart.getData().add(series);
    }

    private void getAggData() throws Exception {
        aggDataList.clear();
        List<Double> totalStakes = getTotalStakesWithoutGouZhaoWu();
        buildAggData(totalStakes, aggDataList, gouZhaoWus);
    }

    private void getAnalysisData() throws Exception {
        analysisDataList.clear();
        List<Double> totalStakes = getTotalStakes();
        buildAggData(totalStakes, analysisDataList, suiDaoGouZhaoWus);
        addHuTongLiJiaoData();
        analysisData();
        calculateSpeed();
        calculateHuTongShiLiTiJiaoCha();
    }

    private List<Double> getTotalStakesWithoutGouZhaoWu() {
        List<Double> startP = pingMianXianXings.stream().map(PingMianXianXing::getStart).collect(Collectors.toList());
        List<Double> startZ = zongMianXianXings.stream().map(ZongMianXianXing::getStart).collect(Collectors.toList());
        List<Double> endP = pingMianXianXings.stream().map(PingMianXianXing::getEnd).collect(Collectors.toList());
        List<Double> endZ = zongMianXianXings.stream().map(ZongMianXianXing::getEnd).collect(Collectors.toList());

        Stream<Double> startStream = Stream.concat(startP.stream(), startZ.stream()).distinct();
        Stream<Double> endStream = Stream.concat(endP.stream(), endZ.stream()).distinct();
        return Stream.concat(startStream, endStream).distinct().sorted().collect(Collectors.toList());
    }

    private List<Double> getTotalStakes() {
        Stream<Double> totalStakes = getTotalStakesWithoutGouZhaoWu().stream();
        List<Double> startG = suiDaoGouZhaoWus.stream().filter(GouZhaoWu::isSuiDao).map(GouZhaoWu::getStart).collect(Collectors.toList());
        List<Double> endG = suiDaoGouZhaoWus.stream().filter(GouZhaoWu::isSuiDao).map(GouZhaoWu::getEnd).collect(Collectors.toList());
        Stream<Double> distinctGouZhaoWu = Stream.concat(startG.stream(), endG.stream()).distinct();
        return Stream.concat(totalStakes, distinctGouZhaoWu).distinct().sorted().collect(Collectors.toList());
    }

    private void buildAggData(List<Double> totalStakes, List<AggData> aggDataList, List<GouZhaoWu> gouZhaoWus) throws Exception {
        for (int i = 1; i < totalStakes.size(); i++) {
            aggDataList.add(AggData.builder()
                    .start(totalStakes.get(i - 1))
                    .end(totalStakes.get(i))
                    .length(totalStakes.get(i) - totalStakes.get(i - 1))
                    .radius(getMatchedRadius(totalStakes.get(i - 1), totalStakes.get(i)))
                    .slope(getMatchedSlope(totalStakes.get(i - 1), totalStakes.get(i)))
                    .roadStructure(getMatchedRoadStructure(totalStakes.get(i - 1), totalStakes.get(i), gouZhaoWus))
                    .build());
        }
    }

    private GouZhaoWuType getMatchedRoadStructure(Double start, Double end, List<GouZhaoWu> gouZhaoWus) throws Exception {
        List<GouZhaoWuType> results = new ArrayList<>();
        for (GouZhaoWu gouZhaoWu : gouZhaoWus) {
            if (start < gouZhaoWu.getStart() && gouZhaoWu.getStart() < end) {
                results.add(gouZhaoWu.getRoadStructure());
            }
            if (start < gouZhaoWu.getEnd() && gouZhaoWu.getEnd() < end) {
                results.add(gouZhaoWu.getRoadStructure());
            }
            if (gouZhaoWu.getStart() <= start && end <= gouZhaoWu.getEnd()) {
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
        analysisDataList = analysisDataList.stream().filter(it -> it.getLength() >= 100).collect(Collectors.toList());
        for (int i = 1; i < analysisDataList.size(); i++) {
            if (!analysisDataList.get(i).getStart().equals(analysisDataList.get(i - 1).getEnd())) {
                analysisDataList.get(i).setStart(analysisDataList.get(i - 1).getEnd());
            }
        }
    }

    private void extensionTunnelStakes() {
        Double qian;
        Double hou;
        Double startStake = suiDaoGouZhaoWus.get(0).getStart();
        Double endStake = suiDaoGouZhaoWus.get(suiDaoGouZhaoWus.size() - 1).getEnd();
        for (int i = 0; i < suiDaoGouZhaoWus.size(); i++) {
            qian = 200D;
            hou = 100D;
            GouZhaoWu suiDaoGouZhaoWu = suiDaoGouZhaoWus.get(i);
            if (suiDaoGouZhaoWu.getRoadStructure() == GouZhaoWuType.SUI) {
                suiDaoGouZhaoWu.setStart(compareAndGetBig(suiDaoGouZhaoWu.getSuiDaoStart(), startStake));
                suiDaoGouZhaoWu.setEnd(compareAndGetSmall(suiDaoGouZhaoWu.getSuiDaoEnd(), endStake));

                for (int j = i - 1; j >= 0; j--) {
                    if (suiDaoGouZhaoWus.get(j).getStart() < suiDaoGouZhaoWu.getStart()) {
                        suiDaoGouZhaoWus.get(j).setEnd(suiDaoGouZhaoWu.getStart());
                        break;
                    } else {
                        qian -= suiDaoGouZhaoWus.get(j).getEnd() - suiDaoGouZhaoWus.get(j).getStart();
                        suiDaoGouZhaoWus.remove(j);
                        i--;
                    }
                }

                for (int k = i + 1; k < suiDaoGouZhaoWus.size(); k++) {
                    if (suiDaoGouZhaoWus.get(k).getEnd() > suiDaoGouZhaoWu.getEnd()) {
                        suiDaoGouZhaoWus.get(k).setStart(suiDaoGouZhaoWu.getEnd());
                        break;
                    } else {
                        hou -= suiDaoGouZhaoWus.get(k).getEnd() - suiDaoGouZhaoWus.get(k).getStart();
                        suiDaoGouZhaoWus.remove(k);
                        k--;
                    }
                }
            }
        }
    }

    private double getEnd(Double endStake, GouZhaoWu currentSuiDaoGouZhao) {
        return currentSuiDaoGouZhao.getEnd() + XiangHou > endStake ? endStake : currentSuiDaoGouZhao.getEnd() + XiangHou;
    }

    private double getStart(Double startStake, GouZhaoWu currentSuiDaoGouZhao) {
        return currentSuiDaoGouZhao.getStart() - XiangQian < startStake ? startStake : currentSuiDaoGouZhao.getStart() - XiangQian;
    }

    private void addHuTongLiJiaoData() {
        for (HuTongLiJiao huTongLiJiao : huTongLiJiaos) {
            for (AggData aggData : analysisDataList) {
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
        analysisDataList.forEach(item -> {
            if (item.getRoadStructure() == GouZhaoWuType.SUI ||
                    item.getRoadStructure() == GouZhaoWuType.QIAO_SUI ||
                    item.getRoadStructure() == GouZhaoWuType.LU_SUI) {
                item.setRoadType(RoadType.SUI_DAO_LU_DUAN);
            } else if (Math.abs(item.getRadius()) > 1000 &&
                    Math.abs(item.getSlope()) < 3 &&
                    item.getLength() > 200) {
                item.setRoadType(RoadType.PING_ZHI_LU_DUAN);
            } else if (Math.abs(item.getRadius()) > 1000 &&
                    Math.abs(item.getSlope()) < 3 &&
                    item.getLength() < 200) {
                item.setRoadType(RoadType.DUAN_PING_ZHI_LU_DUAN);
            } else if (Math.abs(item.getRadius()) > 1000 &&
                    Math.abs(item.getSlope()) >= 3) {
                item.setRoadType(RoadType.ZONG_PU_LU_DUAN);
            } else if (Math.abs(item.getRadius()) <= 1000 &&
                    Math.abs(item.getSlope()) < 3) {
                item.setRoadType(RoadType.PING_QU_LU_DUAN);
            } else if (Math.abs(item.getRadius()) <= 1000 &&
                    Math.abs(item.getSlope()) >= 3) {
                item.setRoadType(RoadType.WAN_PU_LU_DUAN);
            }
        });
    }

    private void calculateSpeed() {
        setUpStartSpeedTemp();
        Double startSpeedTemp;
        for (int i = 0; i < analysisDataList.size(); i++) {
            if (i == 0) {
                startSpeedTemp = this.startSpeed;
            } else {
                startSpeedTemp = analysisDataList.get(i - 1).getEndSpeed();
            }

            if (analysisDataList.get(i).getRoadType() == RoadType.DUAN_PING_ZHI_LU_DUAN) {
                analysisDataList.get(i).setStartSpeed(startSpeedTemp);
                analysisDataList.get(i).setMiddleSpeed(startSpeedTemp);
                analysisDataList.get(i).setEndSpeed(startSpeedTemp);
            } else if (analysisDataList.get(i).getRoadType() == RoadType.PING_ZHI_LU_DUAN) {
                analysisDataList.get(i).setStartSpeed(startSpeedTemp);
                analysisDataList.get(i).setMiddleSpeed(calculateMiddlePingZhiLuDuan(i));
                analysisDataList.get(i).setEndSpeed(calculateEndPingZhiLuDuan(i));
            } else if (analysisDataList.get(i).getRoadType() == RoadType.PING_QU_LU_DUAN) {
                analysisDataList.get(i).setStartSpeed(startSpeedTemp);
                analysisDataList.get(i).setMiddleSpeed(calculateMiddlePingQuLuDuan(i));
                analysisDataList.get(i).setEndSpeed(calculateEndPingQuLuDuan(i));
            } else if (analysisDataList.get(i).getRoadType() == RoadType.ZONG_PU_LU_DUAN) {
                analysisDataList.get(i).setStartSpeed(startSpeedTemp);
                analysisDataList.get(i).setMiddleSpeed(calculateMiddleZongPuLuDuan(i));
                analysisDataList.get(i).setEndSpeed(calculateEndZongPuLuDuan(i));
            } else if (analysisDataList.get(i).getRoadType() == RoadType.WAN_PU_LU_DUAN) {
                analysisDataList.get(i).setStartSpeed(startSpeedTemp);
                analysisDataList.get(i).setMiddleSpeed(calculateMiddleWanPuLuDuan(i));
                analysisDataList.get(i).setEndSpeed(calculateEndWanPuLuDuan(i));
            } else if (analysisDataList.get(i).getRoadType() == RoadType.SUI_DAO_LU_DUAN) {
                analysisDataList.get(i).setStartSpeed(startSpeedTemp);
                analysisDataList.get(i).setMiddleSpeed(calculateMiddleSuiDaoLuDuan(i));
                analysisDataList.get(i).setEndSpeed(calculateEndSuiDaoLuDuan(i));
            }
        }
    }

    private void calculateHuTongShiLiTiJiaoCha() {
        for (int i = 0; i < analysisDataList.size(); i++) {
            AggData currentAggData = analysisDataList.get(i);
            if (currentAggData.getHuTongLiJiao()) {
                if (this.carType == CarType.SMALL) {
                    if (i != 0) {
                        currentAggData.setStartSpeed(currentAggData.getStartSpeed() - 8);
                    }
                    currentAggData.setMiddleSpeed(currentAggData.getMiddleSpeed() - 8);
                    currentAggData.setEndSpeed(currentAggData.getEndSpeed() - 8);
                    if (i != analysisDataList.size() - 1 && !analysisDataList.get(i + 1).getHuTongLiJiao()) {
                        analysisDataList.get(i + 1).setStartSpeed(analysisDataList.get(i + 1).getStartSpeed() - 8);
                    }
                } else {
                    if (i != 0) {
                        currentAggData.setStartSpeed(currentAggData.getStartSpeed() - 5);
                    }
                    currentAggData.setMiddleSpeed(currentAggData.getMiddleSpeed() - 5);
                    currentAggData.setEndSpeed(currentAggData.getEndSpeed() - 5);
                    if (i != analysisDataList.size() - 1 && !analysisDataList.get(i + 1).getHuTongLiJiao()) {
                        analysisDataList.get(i + 1).setStartSpeed(analysisDataList.get(i + 1).getStartSpeed() - 5);
                    }
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
        AggData currentAggData = analysisDataList.get(i);
        double calculateSpeed = 3.6 * Math.sqrt(
                Math.pow((currentAggData.getStartSpeed() / 3.6), 2) + 2 * currentAggData.getLength() *
                        minAcceleration + (maxAcceleration - minAcceleration) * (1 - currentAggData.getStartSpeed() / this.expectSpeed)
        );
        return compareAndGetSmall(calculateSpeed, this.expectSpeed);
    }

    private Double calculateMiddlePingZhiLuDuan(int i) {
        AggData currentAggData = analysisDataList.get(i);
        double calculateSpeed = 3.6 * Math.sqrt(
                Math.pow((currentAggData.getStartSpeed() / 3.6), 2) + 2 * (currentAggData.getLength() / 2) *
                        minAcceleration + (maxAcceleration - minAcceleration) * (1 - currentAggData.getStartSpeed() / this.expectSpeed)
        );
        return compareAndGetSmall(calculateSpeed, this.expectSpeed);
    }

    private Double calculateMiddlePingQuLuDuan(int i) {
        AggData currentAggData = analysisDataList.get(i);
        if (i == 0 || analysisDataList.get(i - 1).getRadius() > 1000) {
            if (this.carType == CarType.SMALL) {
                return -24.212 + 0.834 * currentAggData.getStartSpeed() + 5.729 * calculateMathLog(currentAggData.getRadius());
            } else {
                return -9.432 + 0.963 * currentAggData.getStartSpeed() + 1.522 * calculateMathLog(currentAggData.getRadius());
            }
        } else {
            if (this.carType == CarType.SMALL) {
                return 1.277 + 0.942 * currentAggData.getStartSpeed() + 6.19 * calculateMathLog(currentAggData.getRadius()) - 5.959 * calculateMathLog(analysisDataList.get(i - 1).getRadius());
            } else {
                return -24.472 + 0.990 * currentAggData.getStartSpeed() + 3.629 * calculateMathLog(currentAggData.getRadius());
            }
        }
    }

    private Double calculateEndPingQuLuDuan(int i) {
        AggData currentAggData = analysisDataList.get(i);
        if (i == analysisDataList.size() - 1 || analysisDataList.get(i - 1).getRadius() > 1000) {
            if (this.carType == CarType.SMALL) {
                return 11.946 + 0.908 * currentAggData.getMiddleSpeed();
            } else {
                return 5.217 + 0.926 * currentAggData.getMiddleSpeed();
            }
        } else {
            if (this.carType == CarType.SMALL) {
                return -11.299 + 0.936 * currentAggData.getMiddleSpeed() - 2.060 * calculateMathLog(currentAggData.getRadius()) + 5.203 * calculateMathLog(analysisDataList.get(i - 1).getRadius());
            } else {
                return 5.899 + 0.925 * currentAggData.getMiddleSpeed() - 1.005 * calculateMathLog(currentAggData.getRadius()) + 0.329 * calculateMathLog(analysisDataList.get(i - 1).getRadius());
            }
        }
    }

    private Double calculateMiddleZongPuLuDuan(int i) {
        AggData currentAggData = analysisDataList.get(i);
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
        AggData currentAggData = analysisDataList.get(i);
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
        AggData currentAggData = analysisDataList.get(i);
        AggData preAggData = i == 0 ? analysisDataList.get(i) : analysisDataList.get(i - 1);
        if (i == 0 || preAggData.getRadius() > 1000) {
            if (this.carType == CarType.SMALL) {
                return -31.67 + 0.547 * currentAggData.getStartSpeed() + 11.71 * calculateMathLog(currentAggData.getRadius()) - 0.176 * preAggData.getSlope();
            } else {
                return 1.782 + 0.859 * currentAggData.getStartSpeed() - 0.51 * preAggData.getSlope() + 1.196 * calculateMathLog(currentAggData.getRadius());
            }
        } else {
            if (this.carType == CarType.SMALL) {
                return 0.750 + 0.802 * currentAggData.getStartSpeed() + 2.717 * calculateMathLog(currentAggData.getRadius()) - 0.281 * calculateMathLog(preAggData.getSlope());
            } else {
                return 1.798 + 0.248 * calculateMathLog(currentAggData.getRadius()) + 0.977 * currentAggData.getStartSpeed() - 0.133 * preAggData.getSlope() + 0.23 * calculateMathLog(preAggData.getRadius());
            }
        }
    }

    private Double calculateEndWanPuLuDuan(int i) {
        AggData currentAggData = analysisDataList.get(i);
        AggData preAggData = i == 0 ? analysisDataList.get(i) : analysisDataList.get(i - 1);
        AggData postAggData = i == analysisDataList.size() - 1 ? analysisDataList.get(i) : analysisDataList.get(i + 1);
        if (i == analysisDataList.size() - 1 || preAggData.getRadius() > 1000) {
            if (this.carType == CarType.SMALL) {
                return 27.294 + 0.720 * currentAggData.getMiddleSpeed() - 1.444 * postAggData.getSlope();
            } else {
                return 13.490 + 0.797 * currentAggData.getMiddleSpeed() - 0.6971 * postAggData.getSlope();
            }
        } else {
            if (this.carType == CarType.SMALL) {
                return 1.819 + 0.839 * currentAggData.getMiddleSpeed() + 1.427 * calculateMathLog(currentAggData.getRadius()) + 0.782 * calculateMathLog(postAggData.getRadius()) - 0.48 * postAggData.getSlope();
            } else {
                return 26.837 + 0.109 * calculateMathLog(postAggData.getRadius()) - 3.039 * calculateMathLog(currentAggData.getRadius()) - 0.594 * postAggData.getSlope() + 0.830 * currentAggData.getMiddleSpeed();
            }
        }
    }

    private Double calculateMiddleSuiDaoLuDuan(int i) {
        AggData currentAggData = analysisDataList.get(i);
        if (this.carType == CarType.SMALL) {
            return 0.81 * currentAggData.getStartSpeed() + 8.22;
        } else {
            return 0.85 * currentAggData.getStartSpeed() + 3.89;
        }
    }

    private Double calculateEndSuiDaoLuDuan(int i) {
        AggData currentAggData = analysisDataList.get(i);
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

    private double calculateMathLog(Double radius) {
        return Math.log(Math.abs(radius));
    }
}
