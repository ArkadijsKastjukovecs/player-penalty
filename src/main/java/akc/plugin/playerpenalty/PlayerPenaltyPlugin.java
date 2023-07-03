package akc.plugin.playerpenalty;

import akc.plugin.playerpenalty.commands.AbstractCommand;
import akc.plugin.playerpenalty.commands.CreateIssueCommand;
import akc.plugin.playerpenalty.commands.ForgiveCommand;
import akc.plugin.playerpenalty.commands.PayFineCommand;
import akc.plugin.playerpenalty.handlers.CommandHandler;
import akc.plugin.playerpenalty.handlers.ScheduledTaskHandler;
import akc.plugin.playerpenalty.manager.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class PlayerPenaltyPlugin extends JavaPlugin {

    private List<AbstractCommand> supportedCommands;

    private DiscordSRVManager discordSRVManager;
    private MainConfigManager mainConfigManager;
    private PlayerPointsManager playerPointsManager;
    private TicketManager ticketManager;
    private ValidationManager validationManager;
    private TransformerManager transformerManager;
    private ScheduledTaskHandler scheduledTaskHandler;

    @Override
    public void onEnable() {
        // configuration
        mainConfigManager = new MainConfigManager(this);

        mainConfigManager.populateDefaultValues();
        mainConfigManager.save();

        // handlers
        final var commandHandler = new CommandHandler(this);
        this.ticketManager = new TicketManager(this);
        this.validationManager = new ValidationManager(this);
        this.transformerManager = new TransformerManager(this);
        this.scheduledTaskHandler = new ScheduledTaskHandler();

        // external plugins
        discordSRVManager = new DiscordSRVManager(this);
        discordSRVManager.initDiscordSrv();
        ticketManager.initTicketManager();

        playerPointsManager = new PlayerPointsManager(this);
        playerPointsManager.initPlayerPointsPlugin();

        // commands
        this.supportedCommands = populateCommands();
        commandHandler.registerCommands();
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

    private List<AbstractCommand> populateCommands() {
        return List.of(
                new CreateIssueCommand(this),
                new PayFineCommand(this),
                new ForgiveCommand(this)
        );
    }
}
