package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.Ticket;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;

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
            new Thread(() -> {
                for (int i = 5; i > 0; i--) {
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

            new Thread(() -> {
                for (int i = 5; i > 0; i--) {
                    penaltiesChannel = discordSRV.getJda().getTextChannelsByName("penalties", false)
                            .stream()
                            .findAny().orElse(null);
                    System.out.println("Getting penalties channel: " + accountLinkManager);
                    if (penaltiesChannel != null) {
                        discordMessageSender = new DiscordMessageSender(penaltiesChannel);
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }
    }

    public void sendMEssageToDiscord(Ticket ticket) {
        discordMessageSender.sendMessageToDiscord(ticket);
    }

    public TextChannel getPenaltiesChannel() {
        return penaltiesChannel;
    }
}
