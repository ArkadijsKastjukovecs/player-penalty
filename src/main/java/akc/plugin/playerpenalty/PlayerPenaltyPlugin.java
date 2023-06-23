package akc.plugin.playerpenalty;

import akc.plugin.playerpenalty.commands.AbstractCommand;
import akc.plugin.playerpenalty.commands.BroadcastCommand;
import akc.plugin.playerpenalty.commands.CreateIssueCommand;
import akc.plugin.playerpenalty.handlers.CommandHandler;
import akc.plugin.playerpenalty.manager.DiscordSRVManager;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class PlayerPenaltyPlugin extends JavaPlugin {

    private final List<AbstractCommand> supportedCommands = List.of(
            new CreateIssueCommand(this),
            new BroadcastCommand()
    );

    private DiscordSRVManager discordSRVManager;
    private PlayerPointsAPI playerPointsAPI;
//    private JDA bot;
//    private TextChannel penaltiesChannel;


    @Override
    public void onEnable() {
        final var commandHandler = new CommandHandler(this);
        discordSRVManager = new DiscordSRVManager(this);
        // Plugin startup logic
        if (getServer().getPluginManager().isPluginEnabled("PlayerPoints")) {
            PlayerPoints playerPoints = getPlugin(PlayerPoints.class);
            PlayerPointsAPI playerPointsAPI = new PlayerPointsAPI(playerPoints);
            this.playerPointsAPI = playerPointsAPI;
//            initDiscord();
        }

        discordSRVManager.initDiscordSrv();
        commandHandler.registerCommands();
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


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("SHUTTING DOWN DISCORD BOT");
//        bot.shutdown();
    }

    public List<AbstractCommand> getSupportedCommands() {
        return supportedCommands;
    }

    public DiscordSRVManager getDiscordSRVManager() {
        return discordSRVManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

//        if (command.getName().equals("issue")) {
//            Player targetPlayer = getServer().getPlayer(args[0]);
//            playerPointsAPI.take(targetPlayer.getUniqueId(), Integer.valueOf(args[1]));
//
//            String discordId = accountLinkManager.getDiscordId(targetPlayer.getUniqueId());
//
//            github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel anotherChannel = discordSRV.getJda().getTextChannelsByName("penalties", false)
//                    .stream()
//                    .findAny().orElse(null);
//
//            String messageText = MessageFormat.format("`{0}` выписал штраф игроку: <@{1}>, на сумму: {2}, номер счета: {3}, статус: НЕ ОПЛАЧЕНО", sender.getName(), discordId, args[1], "PLACEHOLDER");
//            System.out.println(messageText);
//            penaltiesChannel.sendMessage(messageText).queue(message -> {
//                message.editMessage(MessageFormat.format("`{0}` выписал штраф игроку: <@{1}>, на сумму: {2}, номер счета: {3}, статус: НЕ ОПЛАЧЕНО", sender.getName(), discordId, args[1], message.getId())).queue();
//                anotherChannel.sendMessage(MessageFormat.format("`{0}` выписал штраф игроку: <@{1}>, на сумму: {2}, номер счета: {3}, статус: НЕ ОПЛАЧЕНО", sender.getName(), discordId, args[1], message.getId())).queue();
//            });
//
//            return true;
//        }
//
//        if (command.getName().equals("pardon")) {
//            penaltiesChannel.retrieveMessageById(args[0]).queue(message -> {
//                message.editMessage(message.getContentRaw().replace("НЕ ОПЛАЧЕНО", "ОПРАВДАНО")).queue();
//            });
//            return true;
//        }
//
//        if (command.getName().equals("pay")) {
//
//            penaltiesChannel.retrieveMessageById(args[0]).queue(message -> {
//                Player targetPlayer = getServer().getPlayer("_Mortem_");
//                boolean take = playerPointsAPI.take(targetPlayer.getUniqueId(), 100);
//                if (take) {
//                    targetPlayer.sendMessage(MessageFormat.format("Штраф под номером: {0} погашен", args[0]));
//                    message.editMessage(message.getContentRaw().replace("НЕ ОПЛАЧЕНО", "ОПЛАЧЕНО")).queue();
//                } else {
//                    targetPlayer.sendMessage(MessageFormat.format("Штраф под номером: {0} не оплачен, возможно не достаточно очков", args[0]));
//                }
//            });
//            return true;
//        }
//
//        return true;
        return true;
    }
}
