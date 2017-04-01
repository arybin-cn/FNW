package info.arybin.fearnotwords;

public enum Config {
    DB_FILE("default_lite.db"),
    FONT_PHONETIC("LucidaSansUnicode.ttf"),
    FONT_ASCII("GothamCondensedLight.otf"),
    FONT_NON_ASCII("FZLanTingHeiGBK.ttf"),
    CURRENT_PLAN("词汇A组");
    private String defaultValue;

    Config(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}