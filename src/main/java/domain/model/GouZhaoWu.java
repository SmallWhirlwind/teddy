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

    public Double getSuiDaoStart() {
        return this.start - 200;
    }

    public Double getSuiDaoEnd() {
        return this.end + 100;
    }
}
