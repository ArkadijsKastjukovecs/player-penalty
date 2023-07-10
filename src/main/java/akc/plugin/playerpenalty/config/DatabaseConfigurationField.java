package akc.plugin.playerpenalty.config;

public enum DatabaseConfigurationField {

    DATABASE_TYPE("sqlite", "Currently supported: sqlite, mysql"),
    DB_IP_ADDRESS("127.0.0.1", "IP address of database"),
    DB_PORT("3306", "port to connect to database"),
    SCHEMA_NAME("player_penalty", "database schema name"),
    DATABASE_LOCAL_FILE_NAME("playerPenalty", "When using sqlite (storing all data at local database) following name will be used"),
    DATABASE_CONNECTION_USER_NAME("sa", "Username used to connect to external database"),
    DATABASE_CONNECTION_PASSWORD("", "Password used to connect to external database"),
    SHOW_AUTO_GENERATED_SQL("false", "Should be used for debugging, switch to 'true' to print auto generated sql queries during execution");


    private final String defaultValue;
    private final String comment;

    DatabaseConfigurationField(String defaultValue, String comment) {
        this.defaultValue = defaultValue;
        this.comment = comment;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getComment() {
        return comment;
    }

    public String getName() {
        var name = this.name().toLowerCase();
        while (name.contains("_")) {
            name = name.replaceFirst("_[a-z]", String.valueOf(Character.toUpperCase(name.charAt(name.indexOf("_") + 1))));
        }
        return name;
    }
}
