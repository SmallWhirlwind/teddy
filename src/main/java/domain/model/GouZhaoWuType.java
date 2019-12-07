package domain.model;

public enum GouZhaoWuType {
    LU("路基路段"),
    QIAO("桥梁路段"),
    SUI("隧道路段"),
    LU_QIAO("路桥路段"),
    LU_SUI("路隧路段"),
    QIAO_SUI("桥隧路段"),
    ERROR("错误类型");

    private String value;

    GouZhaoWuType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static GouZhaoWuType getType(String name) throws Exception {
        if (name.equals("路基路段")) {
            return GouZhaoWuType.LU;
        }
        if (name.equals("桥梁路段")) {
            return GouZhaoWuType.QIAO;
        }
        if (name.equals("隧道路段")) {
            return GouZhaoWuType.SUI;
        }
        throw new Exception(GouZhaoWuType.ERROR.value);
    }
}
