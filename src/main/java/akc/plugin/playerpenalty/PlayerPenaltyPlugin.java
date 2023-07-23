package akc.plugin.playerpenalty;

import akc.plugin.playerpenalty.commands.AbstractCommand;
import akc.plugin.playerpenalty.commands.CreateIssueCommand;
import akc.plugin.playerpenalty.commands.ForgiveCommand;
import akc.plugin.playerpenalty.commands.PayFineCommand;
import akc.plugin.playerpenalty.domain.entities.PlayerEntity;
import akc.plugin.playerpenalty.domain.entities.ScheduledEntity;
import akc.plugin.playerpenalty.domain.entities.TicketEntity;
import akc.plugin.playerpenalty.handlers.CommandHandler;
import akc.plugin.playerpenalty.handlers.ScheduleHandler;
import akc.plugin.playerpenalty.manager.DatabaseConfigManager;
import akc.plugin.playerpenalty.manager.DatabaseConnectionManager;
import akc.plugin.playerpenalty.manager.DiscordSRVManager;
import akc.plugin.playerpenalty.manager.FlywayMigrationManager;
import akc.plugin.playerpenalty.manager.MainConfigManager;
import akc.plugin.playerpenalty.manager.PlayerPointsManager;
import akc.plugin.playerpenalty.manager.TransformerManager;
import akc.plugin.playerpenalty.manager.ValidationManager;
import akc.plugin.playerpenalty.repository.ScheduleRepository;
import akc.plugin.playerpenalty.repository.TicketRepository;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class PlayerPenaltyPlugin extends JavaPlugin {

    private final List<Class<?>> supportedEntities = createSupportedEntities();

    private List<AbstractCommand> supportedCommands;

    private DiscordSRVManager discordSRVManager;

    private MainConfigManager mainConfigManager;
    private DatabaseConfigManager databaseConfigManager;
    private PlayerPointsManager playerPointsManager;
    private ValidationManager validationManager;
    private TransformerManager transformerManager;
    private DatabaseConnectionManager databaseConnectionManager;
    private TicketRepository ticketRepository;
    private ScheduleRepository scheduleRepository;
    private ScheduleHandler scheduleHandler;

    @Override
    public void onEnable() {
        // configuration
        mainConfigManager = new MainConfigManager(this);
        databaseConfigManager = new DatabaseConfigManager(this);

        mainConfigManager.populateDefaultValues();
        mainConfigManager.save();

        databaseConfigManager.populateDefaultValues();
        databaseConfigManager.save();

        // database migration
        new FlywayMigrationManager().migrate(this);

        // handlers
        final var commandHandler = new CommandHandler(this);
        this.databaseConnectionManager = new DatabaseConnectionManager(this);

        // external plugins
        discordSRVManager = new DiscordSRVManager(this);
        discordSRVManager.initDiscordSrv();
        databaseConnectionManager.initializeSessionFactory();

        playerPointsManager = new PlayerPointsManager(this);
        playerPointsManager.initPlayerPointsPlugin();

        // database
        ticketRepository = new TicketRepository(this);
        scheduleRepository = new ScheduleRepository(this);
        scheduleHandler = new ScheduleHandler(this);
        scheduleHandler.schedulePassedRepeatingTasks();

        // command handling
        this.validationManager = new ValidationManager();
        this.transformerManager = new TransformerManager(this);

        // commands
        this.supportedCommands = populateCommands();
        commandHandler.registerCommands();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        databaseConnectionManager.closeConnection();
    }

    public MainConfigManager getConfigManager() {
        return mainConfigManager;
    }


    public List<AbstractCommand> getSupportedCommands() {
        return supportedCommands;
    }

    public DiscordSRVManager getDiscordSRVManager() {
        return discordSRVManager;
    }

    public PlayerPointsManager getPlayerPointsManager() {
        return playerPointsManager;
    }


    public ValidationManager getValidationManager() {
        return validationManager;
    }

    public TransformerManager getTransformerManager() {
        return transformerManager;
    }

    public DatabaseConnectionManager getDatabaseConnectionManager() {
        return databaseConnectionManager;
    }

    public DatabaseConfigManager getDatabaseConfigManager() {
        return databaseConfigManager;
    }

    public List<Class<?>> getSupportedEntities() {
        return supportedEntities;
    }

    public TicketRepository getTicketRepository() {
        return ticketRepository;
    }

    public ScheduleRepository getScheduleRepository() {
        return scheduleRepository;
    }

    public ScheduleHandler getScheduleHandler() {
        return scheduleHandler;
    }

    private List<AbstractCommand> populateCommands() {
        return List.of(
                new CreateIssueCommand(this),
                new PayFineCommand(this),
                new ForgiveCommand(this)
        );
    }

    private List<Class<?>> createSupportedEntities() {
        return List.of(
                TicketEntity.class,
                PlayerEntity.class,
                ScheduledEntity.class
        );
    }
}
