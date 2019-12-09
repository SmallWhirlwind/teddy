package domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggData {
    private Double start;
    private Double end;
    private Double length;
    private Double radius;
    private Double slope;
    private GouZhaoWuType roadStructure;
    @Builder.Default
    private Boolean huTongLiJiao = false;
    private RoadType roadType;
    private Double startSpeed;
    private Double middleSpeed;
    private Double endSpeed;
    private RoadSecurity roadSecurity;

    public String getHuTongLiJiaoValue() {
        return this.huTongLiJiao ? "互通立交" : "";
    }
}
