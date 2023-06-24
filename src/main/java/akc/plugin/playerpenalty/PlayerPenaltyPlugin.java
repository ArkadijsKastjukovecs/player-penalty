package akc.plugin.playerpenalty;

import akc.plugin.playerpenalty.commands.AbstractCommand;
import akc.plugin.playerpenalty.commands.CreateIssueCommand;
import akc.plugin.playerpenalty.commands.PayFineCommand;
import akc.plugin.playerpenalty.handlers.CommandHandler;
import akc.plugin.playerpenalty.manager.DiscordSRVManager;
import akc.plugin.playerpenalty.manager.MainConfigManager;
import akc.plugin.playerpenalty.manager.PlayerPointsManager;
import akc.plugin.playerpenalty.manager.TicketManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class PlayerPenaltyPlugin extends JavaPlugin {

    private List<AbstractCommand> supportedCommands;

    private DiscordSRVManager discordSRVManager;
    private MainConfigManager mainConfigManager;
    private PlayerPointsManager playerPointsManager;
    private TicketManager ticketManager;

    @Override
    public void onEnable() {
        // configuration
        mainConfigManager = new MainConfigManager(this);

        mainConfigManager.populateDefaultValues();
        mainConfigManager.save();

        // handlers
        final var commandHandler = new CommandHandler(this);
        this.ticketManager = new TicketManager(this);

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

//    private void initDiscord() {
//        try {
//            bot = JDABuilder.createDefault("MTExMjAwMDYzMTE3NjE3MTUyMg.GqZM1Q.HIT3Kml7NygfmwJuF9IBU8ULtBMraugzm4qeBg")
//                    .setMemberCachePolicy(MemberCachePolicy.NONE)
//                    .build().awaitReady();
//            penaltiesChannel = bot.getTextChannelsByName("penalties", false)
//                    .stream()
//                    .findAny().orElse(null);
//            System.out.println("printing channel id: " + penaltiesChannel.getId());
//            System.out.println(penaltiesChannel);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }


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

    private List<AbstractCommand> populateCommands() {
        return List.of(
                new CreateIssueCommand(this),
                new PayFineCommand(this)
        );
    }
}
