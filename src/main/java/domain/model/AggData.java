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
}
