package domain.model;

public enum RoadType {
    SUI_DAO_LU_DUAN("隧道路段"),
    PING_ZHI_LU_DUAN("平直路段"),
    DUAN_PING_ZHI_LU_DUAN("短平直路段"),
    ZONG_PU_LU_DUAN("纵坡路段"),
    PING_QU_LU_DUAN("平曲线路段"),
    WAN_PU_LU_DUAN("弯坡组合路段");

    private String value;

    RoadType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
