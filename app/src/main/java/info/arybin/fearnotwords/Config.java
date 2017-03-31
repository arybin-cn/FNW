package info.arybin.fearnotwords;

public enum Config {
    DB_FILE("default_lite.db"),
    FONT("GothamCondensed_Light.otf"),
    FONT_NON_ASCII("FZLanTingHei_GBK.ttf"),
    CURRENT_PLAN("词汇A组");
    private String defaultValue;

    Config(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}