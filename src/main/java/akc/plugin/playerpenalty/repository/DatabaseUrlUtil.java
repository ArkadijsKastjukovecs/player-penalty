package akc.plugin.playerpenalty.repository;

import akc.plugin.playerpenalty.config.DatabaseConfigurationField;
import akc.plugin.playerpenalty.domain.SupportedDatabaseType;
import akc.plugin.playerpenalty.manager.DatabaseConfigManager;

import java.io.File;
import java.util.Optional;

public class DatabaseUrlUtil {

    private static final String SEPARATOR = File.separator;

    private DatabaseUrlUtil() {
    }

    public static String getDatabaseUrlFromConfig(DatabaseConfigManager databaseConfigManager) {
        final var supportedDatabaseType = resolveSupportedDatabaseType(databaseConfigManager);

        return switch (supportedDatabaseType) {
            case SQLITE -> getSqliteConnectionUrl(databaseConfigManager);
            case MYSQL -> getMysqlConnectionUrl(databaseConfigManager);
        };
    }

    public static SupportedDatabaseType resolveSupportedDatabaseType(DatabaseConfigManager databaseConfigManager) {
        return Optional.ofNullable(databaseConfigManager)
                .map(it -> it.getConfigValue(DatabaseConfigurationField.DATABASE_TYPE))
                .map(SupportedDatabaseType::byName)
                .orElseThrow(() -> new UnsupportedOperationException("Chosen database type is not supported"));
    }

    private static String getSqliteConnectionUrl(DatabaseConfigManager databaseConfigManager) {
        final var stringBuilder = new StringBuilder();
        final var pluginDirectory = databaseConfigManager.getWorkingDirectory();
        final var databaseFileName = databaseConfigManager.getConfigValue(DatabaseConfigurationField.DATABASE_LOCAL_FILE_NAME);
        return stringBuilder.append("jdbc:sqlite:")
                .append(pluginDirectory)
                .append(SEPARATOR)
                .append(databaseFileName)
                .append(".sqlite")
                .toString();

    }

    private static String getMysqlConnectionUrl(DatabaseConfigManager databaseConfigManager) {
        final var stringBuilder = new StringBuilder();
        final var databaseIP = databaseConfigManager.getConfigValue(DatabaseConfigurationField.DB_IP_ADDRESS);
        final var databasePort = databaseConfigManager.getConfigValue(DatabaseConfigurationField.DB_PORT);
        final var databaseSchema = databaseConfigManager.getConfigValue(DatabaseConfigurationField.SCHEMA_NAME);
        return stringBuilder.append("jdbc:mysql:")
                .append("//")
//                .append(SEPARATOR)
                .append(databaseIP)
                .append(":")
                .append(databasePort)
                .append("/")
                .append(databaseSchema)
                .toString();
    }
}
