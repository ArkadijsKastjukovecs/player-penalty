package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.DatabaseConfigurationField;
import akc.plugin.playerpenalty.repository.DatabaseUrlUtil;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

public class FlywayMigrationManager {

    public void migrate(PlayerPenaltyPlugin plugin) {
        final var databaseConfigManager = plugin.getDatabaseConfigManager();
        final var supportedDatabaseType = DatabaseUrlUtil.resolveSupportedDatabaseType(databaseConfigManager);

        final var flywayConfiguration = Flyway.configure(this.getClass().getClassLoader());

        switch (supportedDatabaseType) {
            case SQLITE -> populateSqliteDataSource(flywayConfiguration, databaseConfigManager);
            case MYSQL -> populateMysqlDataSource(flywayConfiguration, databaseConfigManager);
        }

        flywayConfiguration
                .load()
                .migrate();
    }

    private void populateSqliteDataSource(FluentConfiguration configuration, DatabaseConfigManager databaseConfigManager) {
        final var databaseUrlFromConfig = DatabaseUrlUtil.getDatabaseUrlFromConfig(databaseConfigManager);
        configuration.dataSource(databaseUrlFromConfig, null, null);
    }

    private void populateMysqlDataSource(FluentConfiguration configuration, DatabaseConfigManager databaseConfigManager) {
        final var databaseUrlFromConfig = DatabaseUrlUtil.getDatabaseUrlFromConfig(databaseConfigManager);
        final var userName = databaseConfigManager.getConfigValue(DatabaseConfigurationField.DATABASE_CONNECTION_USER_NAME);
        final var password = databaseConfigManager.getConfigValue(DatabaseConfigurationField.DATABASE_CONNECTION_PASSWORD);
        configuration.dataSource(databaseUrlFromConfig, userName, password);
    }
}
