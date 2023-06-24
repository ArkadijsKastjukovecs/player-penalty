package akc.plugin.playerpenalty.config;

public enum ConfigurationFields {
    DISCORD_CHANNEL_NAME("penalties"),
    TIME_DISPLAY_FORMAT("EEE MMM dd HH:mm:ss yyyy"),
    CURRENT_ZONE_ID("Europe/Riga"),
    LOCALE("ru");


    private final String defaultValue;

    ConfigurationFields(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getName() {
        var name = this.name().toLowerCase();
        while (name.contains("_")) {
            name = name.replaceFirst("_[a-z]", String.valueOf(Character.toUpperCase(name.charAt(name.indexOf("_") + 1))));
        }
        return name;
    }
}
