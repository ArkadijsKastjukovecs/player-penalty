package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.DatabaseConfigurationField;
import akc.plugin.playerpenalty.domain.entities.TicketEntity;
import akc.plugin.playerpenalty.repository.DatabaseUrlUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

public class DatabaseConnectionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnectionManager.class);
    private final DatabaseConfigManager databaseConfigManager;
    private final List<Class<?>> supportedEntities;
    private SessionFactory concreteSessionFactory;

    public DatabaseConnectionManager(PlayerPenaltyPlugin plugin) {
        this.databaseConfigManager = plugin.getDatabaseConfigManager();
        this.supportedEntities = plugin.getSupportedEntities();
    }

    public void initializeSessionFactory() {
        try {
            final var databaseUrlFromConfig = DatabaseUrlUtil.getDatabaseUrlFromConfig(databaseConfigManager);
            final var supportedDatabaseType = DatabaseUrlUtil.resolveSupportedDatabaseType(databaseConfigManager);
            final var dbUserName = databaseConfigManager.getConfigValue(DatabaseConfigurationField.DATABASE_CONNECTION_USER_NAME);
            final var dbPassword = databaseConfigManager.getConfigValue(DatabaseConfigurationField.DATABASE_CONNECTION_PASSWORD);
            final var showSql = databaseConfigManager.getConfigValue(DatabaseConfigurationField.SHOW_AUTO_GENERATED_SQL);

            final var prop = new Properties();
            prop.setProperty("hibernate.connection.url", databaseUrlFromConfig);
            prop.setProperty("hibernate.connection.username", dbUserName);
            prop.setProperty("hibernate.connection.password", dbPassword);
            prop.setProperty("hibernate.dialect", supportedDatabaseType.getDialect());
            prop.setProperty("hibernate.show_sql", showSql);
            prop.setProperty("hibernate.format_sql", "true");
            prop.setProperty("connection.provider_class", "com.zaxxer.hikari.hibernate.HikariConnectionProvider");
            prop.setProperty("hibernate.connection.autocommit", "true");
//            prop.setProperty("hibernate.hbm2ddl.auto", "create");


            final var hibernateConfiguration = new Configuration()
                    .addProperties(prop)
                    .addPackage("akc.plugin.playerpenalty.domain.entities.");

            supportedEntities.forEach(hibernateConfiguration::addAnnotatedClass);

            concreteSessionFactory = hibernateConfiguration.buildSessionFactory();
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public Session getSession() throws HibernateException {
        return concreteSessionFactory.openSession();
    }

    public void saveTicketToDb(TicketEntity entity) {
        final var session = getSession();
        session.beginTransaction();
        final var saved = session.save(entity);
//        final List<TicketEntity> query = session.createQuery("SELECT t from TicketEntity t", TicketEntity.class).getResultList();
        session.getTransaction().commit();
        LOGGER.info("saved entity");
        session.close();
    }
}
