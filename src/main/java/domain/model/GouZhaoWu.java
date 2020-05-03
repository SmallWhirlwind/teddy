package domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GouZhaoWu {
    private Double start;
    private Double end;
    private GouZhaoWuType roadStructure;

    public boolean isSuiDao() {
        return this.roadStructure == GouZhaoWuType.SUI;
    }

    public Double getSuiDaoStart(boolean isZhengXiang) {
        if(!isZhengXiang) {
            return this.start + 200;
        }
        return this.start - 200;
    }

    public Double getSuiDaoEnd(boolean isZhengXiang) {
        if(!isZhengXiang) {
            return this.end - 100;
        }
        return this.end + 100;
    }

    public GouZhaoWu clone() {
        return GouZhaoWu.builder()
                .start(this.getStart())
                .end(this.getEnd())
                .roadStructure(this.roadStructure)
                .build();
    }
}
