package domain.model;

public enum RoadType {
    SUI_DAO_LU_DUAN("隧道路段"),
    HU_TONG_LI_JIAO_LU_DUAN("互通立交路段"),
    PING_ZHI_LU_DUAN("平直路段"),
    DUAN_PING_ZHI_LU_DUAN("短平直路段"),
    ZONG_PU_LU_DUAN("纵坡路段"),
    PING_QU_LU_DUAN("平区路段"),
    WAN_PU_LU_DUAN("弯坡路段");

    private String value;

    RoadType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
