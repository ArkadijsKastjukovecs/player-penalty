package akc.plugin.playerpenalty;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

public final class PlayerPenalty extends JavaPlugin {

    private PlayerPointsAPI playerPointsAPI;
    private JDA bot;
    private TextChannel penaltiesChannel;
    private AccountLinkManager accountLinkManager;
    private DiscordSRV discordSRV;

    @Override
    public void onEnable() {
        TabCompleter
        // Plugin startup logic
        if (getServer().getPluginManager().isPluginEnabled("PlayerPoints")) {
            PlayerPoints playerPoints = getPlugin(PlayerPoints.class);
            System.out.println("PlayerPoint plugin successfully obtained! ARK");
            PlayerPointsAPI playerPointsAPI = new PlayerPointsAPI(playerPoints);
            this.playerPointsAPI = playerPointsAPI;
            initDiscord();
            initDiscordSrv();
        }

//        getServer().getPluginManager()
    }

    private void initDiscord() {
        try {
            bot = JDABuilder.createDefault("MTExMjAwMDYzMTE3NjE3MTUyMg.GqZM1Q.HIT3Kml7NygfmwJuF9IBU8ULtBMraugzm4qeBg")
                    .addEventListeners(new DiscordListener())
                    .setMemberCachePolicy(MemberCachePolicy.NONE)
                    .build().awaitReady();
            penaltiesChannel = bot.getTextChannelsByName("penalties", false)
                    .stream()
                    .findAny().orElse(null);
            System.out.println("printing channel id: " + penaltiesChannel.getId());
            System.out.println(penaltiesChannel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initDiscordSrv() {
        if (getServer().getPluginManager().isPluginEnabled("PlayerPoints")) {
            discordSRV = DiscordSRV.getPlugin();
            System.out.println("DISCORDSRV: " + discordSRV);
            new Thread(() -> {
                for (int i = 5; i > 0 ; i--) {
                    accountLinkManager = discordSRV.getAccountLinkManager();
                    System.out.println("Getting accountLinkManager: " + accountLinkManager);
                    if (accountLinkManager != null) {
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
//            accountLinkManager = discordSRV.getAccountLinkManager();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("SHUTTING DOWN DISCORD BOT");
        bot.shutdown();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equals("issue")) {
            Player targetPlayer = getServer().getPlayer(args[0]);
            playerPointsAPI.take(targetPlayer.getUniqueId(), Integer.valueOf(args[1]));

            String discordId = accountLinkManager.getDiscordId(targetPlayer.getUniqueId());

            github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel anotherChannel = discordSRV.getJda().getTextChannelsByName("penalties", false)
                    .stream()
                    .findAny().orElse(null);

            String messageText = MessageFormat.format("`{0}` выписал штраф игроку: <@{1}>, на сумму: {2}, номер счета: {3}, статус: НЕ ОПЛАЧЕНО", sender.getName(), discordId, args[1], "PLACEHOLDER");
            System.out.println(messageText);
            penaltiesChannel.sendMessage(messageText).queue(message -> {
                message.editMessage(MessageFormat.format("`{0}` выписал штраф игроку: <@{1}>, на сумму: {2}, номер счета: {3}, статус: НЕ ОПЛАЧЕНО", sender.getName(), discordId, args[1], message.getId())).queue();
                anotherChannel.sendMessage(MessageFormat.format("`{0}` выписал штраф игроку: <@{1}>, на сумму: {2}, номер счета: {3}, статус: НЕ ОПЛАЧЕНО", sender.getName(), discordId, args[1], message.getId())).queue();
            });

            return true;
        }

        if (command.getName().equals("pardon")) {
            penaltiesChannel.retrieveMessageById(args[0]).queue(message -> {
                message.editMessage(message.getContentRaw().replace("НЕ ОПЛАЧЕНО", "ОПРАВДАНО")).queue();
            });
            return true;
        }

        if (command.getName().equals("pay")) {

            penaltiesChannel.retrieveMessageById(args[0]).queue(message -> {
                Player targetPlayer = getServer().getPlayer("_Mortem_");
                boolean take = playerPointsAPI.take(targetPlayer.getUniqueId(), 100);
                if (take) {
                    targetPlayer.sendMessage(MessageFormat.format("Штраф под номером: {0} погашен", args[0]));
                    message.editMessage(message.getContentRaw().replace("НЕ ОПЛАЧЕНО", "ОПЛАЧЕНО")).queue();
                } else {
                    targetPlayer.sendMessage(MessageFormat.format("Штраф под номером: {0} не оплачен, возможно не достаточно очков", args[0]));
                }
            });
            return true;
        }

        return true;
    }
}

class DiscordListener extends ListenerAdapter {

}
