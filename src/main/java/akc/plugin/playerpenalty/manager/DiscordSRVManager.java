package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.ConfigurationFields;
import akc.plugin.playerpenalty.domain.Ticket;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.ISnowflake;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class DiscordSRVManager {

    private final PlayerPenaltyPlugin plugin;
    private final String channelToSendMessages;
    private AccountLinkManager accountLinkManager;
    private DiscordSRV discordSRV;
    private TextChannel penaltiesChannel;
    private DiscordMessageSender discordMessageSender;

    public DiscordSRVManager(PlayerPenaltyPlugin playerPenaltyPlugin) {
        this.plugin = playerPenaltyPlugin;
        this.channelToSendMessages = plugin.getConfigManager().getConfigValue(ConfigurationFields.DISCORD_CHANNEL_NAME);
    }

    public void initDiscordSrv() {
            discordSRV = DiscordSRV.getPlugin();
            System.out.println("DISCORDSRV: " + discordSRV);

            Bukkit.getScheduler().runTask(plugin, () -> {
                for (int i = 5; i >= 0; i--) {
                    this.accountLinkManager = discordSRV.getAccountLinkManager();
                    if (accountLinkManager != null) {
                        System.out.println("Account link manager successfully obtained");
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
                for (int i = 5; i >= 0; i--) {
                    this.penaltiesChannel = discordSRV.getJda().getTextChannelsByName(channelToSendMessages, false)
                            .stream()
                            .findAny().orElse(null);
                    System.out.println("Channel to send messages is not obtained");
                    if (penaltiesChannel != null) {
                        System.out.println("Channel to send messages successfully obtained");
                        discordMessageSender = new DiscordMessageSender(plugin, getPenaltiesChannel());
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

    public List<String> getTickets(Player player) {
        final var discordId = getDiscordId(player);
        return getPenaltiesChannel().getIterableHistory().stream()
                .filter(message -> message.getMentionedUsers().stream()
                        .anyMatch(user -> user.getId().equals(discordId)))
                .map(ISnowflake::getId)
                .collect(Collectors.toList());
    }

    public Message getMessage(String messageId) {
        return getPenaltiesChannel().getHistory().getMessageById(messageId);
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
