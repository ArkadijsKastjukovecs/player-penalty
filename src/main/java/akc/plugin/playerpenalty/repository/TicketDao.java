package akc.plugin.playerpenalty.repository;

import akc.plugin.playerpenalty.domain.entities.TicketEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class TicketDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TicketDao.class);
    private SessionFactory concreteSessionFactory;

    public void initTicketDao() {
        try {
            Properties prop = new Properties();
            prop.setProperty("hibernate.connection.url", "jdbc:sqlite:./plugins/PlayerPenalty/demodb.sqlite");
            prop.setProperty("hibernate.connection.username", "sa");
//            prop.setProperty("hibernate.connection.password", "go");
            prop.setProperty("hibernate.dialect", "org.sqlite.hibernate.dialect.SQLiteDialect");
            prop.setProperty("hibernate.show_sql", "true");
            prop.setProperty("hibernate.format_sql", "true");
            prop.setProperty("connection.provider_class", "com.zaxxer.hikari.hibernate.HikariConnectionProvider");

            prop.setProperty("hibernate.hbm2ddl.auto", "validate");
            concreteSessionFactory = new org.hibernate.cfg.Configuration()
                    .addProperties(prop)
                    //.addPackage("com.kat")
                    .addAnnotatedClass(TicketEntity.class)
                    .buildSessionFactory()
            ;
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
        session.getTransaction().commit();
        LOGGER.info("saved entity");
        session.close();
    }

}
