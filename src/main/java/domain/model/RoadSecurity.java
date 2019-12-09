package domain.model;

public enum RoadSecurity {
    GOOD("好"),
    OK("较好"),
    BAD("不良");

    private String value;

    RoadSecurity(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
