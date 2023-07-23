package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.DatabaseConfigurationField;
import akc.plugin.playerpenalty.domain.SupportedDatabaseType;
import akc.plugin.playerpenalty.repository.DatabaseUrlUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

public class FlywayMigrationManager {

    private static final String DB_MIGRATION_TARGET_FORMAT = "/db/migration/%s";

    public void migrate(PlayerPenaltyPlugin plugin) {
        final var databaseConfigManager = plugin.getDatabaseConfigManager();
        final var supportedDatabaseType = DatabaseUrlUtil.resolveSupportedDatabaseType(databaseConfigManager);

        final var flywayConfiguration = Flyway.configure(plugin.getClass().getClassLoader());

        switch (supportedDatabaseType) {
            case SQLITE -> populateSqliteDataSource(flywayConfiguration, databaseConfigManager);
            case MYSQL -> populateMysqlDataSource(flywayConfiguration, databaseConfigManager);
        }

        flywayConfiguration
                .locations(getLocation(supportedDatabaseType))
                .load()
                .migrate();
    }

    private String getLocation(SupportedDatabaseType databaseType) {
        return DB_MIGRATION_TARGET_FORMAT.formatted(databaseType.name().toLowerCase());
    }

    private void populateSqliteDataSource(FluentConfiguration configuration, DatabaseConfigManager databaseConfigManager) {
        final var databaseUrlFromConfig = DatabaseUrlUtil.getDatabaseUrlFromConfig(databaseConfigManager);
        configuration.dataSource(databaseUrlFromConfig, null, null);
    }

    private void populateMysqlDataSource(FluentConfiguration configuration, DatabaseConfigManager databaseConfigManager) {
        final var databaseUrlFromConfig = DatabaseUrlUtil.getDatabaseUrlFromConfig(databaseConfigManager);
        final var userName = databaseConfigManager.getConfigValue(DatabaseConfigurationField.DATABASE_CONNECTION_USER_NAME);
        final var password = databaseConfigManager.getConfigValue(DatabaseConfigurationField.DATABASE_CONNECTION_PASSWORD);

        final var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(databaseUrlFromConfig);
        hikariConfig.setUsername(userName);
        hikariConfig.setPassword(password);
        configuration.cleanDisabled(false);
        configuration.dataSource(new HikariDataSource(hikariConfig));
    }
}
