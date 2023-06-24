package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.Ticket;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DiscordSRVManager {

    private final PlayerPenaltyPlugin plugin;
    private AccountLinkManager accountLinkManager;
    private DiscordSRV discordSRV;
    private TextChannel penaltiesChannel;
    private DiscordMessageSender discordMessageSender;

    public DiscordSRVManager(PlayerPenaltyPlugin playerPenaltyPlugin) {
        this.plugin = playerPenaltyPlugin;
    }

    public void initDiscordSrv() {
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlayerPoints")) {
            discordSRV = DiscordSRV.getPlugin();
            System.out.println("DISCORDSRV: " + discordSRV);

            Bukkit.getScheduler().runTask(plugin, () -> {
                for (int i = 5; i > 0; i--) {
                    accountLinkManager = discordSRV.getAccountLinkManager();
                    System.out.println("Getting accountLinkManager: " + accountLinkManager);
                    if (accountLinkManager != null) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            Bukkit.getScheduler().runTask(plugin, () -> {
                for (int i = 5; i > 0; i--) {
                    penaltiesChannel = discordSRV.getJda().getTextChannelsByName("penalties", false)
                            .stream()
                            .findAny().orElse(null);
                    System.out.println("Getting penalties channel: " + penaltiesChannel);
                    if (penaltiesChannel != null) {
                        discordMessageSender = new DiscordMessageSender(penaltiesChannel);
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    public String getDiscordId(Player player) {
        return accountLinkManager.getDiscordId(player.getUniqueId());
    }

    public void sendMEssageToDiscord(Ticket ticket) {
        discordMessageSender.sendMessageToDiscord(ticket);
    }

    public TextChannel getPenaltiesChannel() {
        return penaltiesChannel;
    }
}
