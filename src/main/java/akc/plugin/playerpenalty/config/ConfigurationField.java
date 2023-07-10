package akc.plugin.playerpenalty.config;

public enum ConfigurationField {
    DISCORD_CHANNEL_NAME("penalties"),
    TIME_DISPLAY_FORMAT("EEE MMM dd HH:mm:ss yyyy"),
    CURRENT_ZONE_ID("Europe/Riga"),
    LOCALE("ru"),
    CONNECTION_RETRY_COUNT("5"),
    SKIN_API("https://crafatar.com/renders/body/%s");

    private final String defaultValue;

    ConfigurationField(String defaultValue) {
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
