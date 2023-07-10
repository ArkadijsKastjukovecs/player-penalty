package akc.plugin.playerpenalty.domain;

import java.util.Arrays;

public enum SupportedDatabaseType {
    SQLITE("org.sqlite.hibernate.dialect.SQLiteDialect"),
    MYSQL("org.hibernate.dialect.MySQL5Dialect");

    private final String dialect;

    SupportedDatabaseType(String dialect) {
        this.dialect = dialect;
    }

    public String getDialect() {
        return dialect;
    }

    public static SupportedDatabaseType byName(String name) {
        return Arrays.stream(SupportedDatabaseType.values())
                .filter(it -> name.equalsIgnoreCase(it.name().toLowerCase()))
                .findFirst()
                .orElse(null);
    }
}
