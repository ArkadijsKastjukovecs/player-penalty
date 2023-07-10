package akc.plugin.playerpenalty;

import akc.plugin.playerpenalty.commands.AbstractCommand;
import akc.plugin.playerpenalty.commands.CreateIssueCommand;
import akc.plugin.playerpenalty.commands.ForgiveCommand;
import akc.plugin.playerpenalty.commands.PayFineCommand;
import akc.plugin.playerpenalty.domain.entities.PlayerEntity;
import akc.plugin.playerpenalty.domain.entities.TicketEntity;
import akc.plugin.playerpenalty.handlers.CommandHandler;
import akc.plugin.playerpenalty.handlers.ScheduledTaskHandler;
import akc.plugin.playerpenalty.manager.DatabaseConfigManager;
import akc.plugin.playerpenalty.manager.DatabaseConnectionManager;
import akc.plugin.playerpenalty.manager.DiscordSRVManager;
import akc.plugin.playerpenalty.manager.MainConfigManager;
import akc.plugin.playerpenalty.manager.PlayerPointsManager;
import akc.plugin.playerpenalty.manager.TicketManager;
import akc.plugin.playerpenalty.manager.TransformerManager;
import akc.plugin.playerpenalty.manager.ValidationManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class PlayerPenaltyPlugin extends JavaPlugin {

    private final List<Class<?>> supportedEntities = createSupportedEntities();

    private List<AbstractCommand> supportedCommands;

    private DiscordSRVManager discordSRVManager;

    private MainConfigManager mainConfigManager;
    private DatabaseConfigManager databaseConfigManager;
    private PlayerPointsManager playerPointsManager;
    private TicketManager ticketManager;
    private ValidationManager validationManager;
    private TransformerManager transformerManager;
    private ScheduledTaskHandler scheduledTaskHandler;
    private DatabaseConnectionManager databaseConnectionManager;

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
//        new FlywayMigrationManager().migrate(this);

        // handlers
        final var commandHandler = new CommandHandler(this);
        this.ticketManager = new TicketManager(this);
        this.validationManager = new ValidationManager(this);
        this.transformerManager = new TransformerManager(this);
        this.scheduledTaskHandler = new ScheduledTaskHandler();
        this.databaseConnectionManager = new DatabaseConnectionManager(this);

        // external plugins
        discordSRVManager = new DiscordSRVManager(this);
        discordSRVManager.initDiscordSrv();
        ticketManager.initTicketManager();
        databaseConnectionManager.initializeSessionFactory();

        playerPointsManager = new PlayerPointsManager(this);
        playerPointsManager.initPlayerPointsPlugin();

        // commands
        this.supportedCommands = populateCommands();
        commandHandler.registerCommands();

//        databaseConnectionManager.saveTicketToDb(new TicketEntity().setValue("value"));
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

    public TicketManager getTicketManager() {
        return ticketManager;
    }

    public ValidationManager getValidationManager() {
        return validationManager;
    }

    public TransformerManager getTransformerManager() {
        return transformerManager;
    }

    public ScheduledTaskHandler getScheduledTaskHandler() {
        return scheduledTaskHandler;
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
                PlayerEntity.class
        );
    }
}
