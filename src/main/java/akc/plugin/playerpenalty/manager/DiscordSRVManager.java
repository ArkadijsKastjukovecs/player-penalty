package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.ConfigurationField;
import akc.plugin.playerpenalty.domain.entities.TicketEntity;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DiscordSRVManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordSRVManager.class);

    private final PlayerPenaltyPlugin plugin;
    private final String channelToSendMessages;
    private final int connectionRetryCount;
    private AccountLinkManager accountLinkManager;
    private DiscordSRV discordSRV;
    private TextChannel penaltiesChannel;
    private DiscordMessageSender discordMessageSender;

    public DiscordSRVManager(PlayerPenaltyPlugin playerPenaltyPlugin) {
        this.plugin = playerPenaltyPlugin;
        this.channelToSendMessages = plugin.getConfigManager().getConfigValue(ConfigurationField.DISCORD_CHANNEL_NAME);
        this.connectionRetryCount = getConnectionRetryCount();
    }

    public void initDiscordSrv() {
        this.discordSRV = JavaPlugin.getPlugin(DiscordSRV.class);
        LOGGER.debug("Getting DiscordSrv plugin");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::obtainAccountLinkManager);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::obtainChannelToSendPenalties);
    }

    public String getDiscordId(Player player) {
        return accountLinkManager.getDiscordId(player.getUniqueId());
    }

    public void sendMEssageToDiscord(TicketEntity ticket) {
        discordMessageSender.sendMessageToDiscord(ticket);
    }

    public TextChannel getPenaltiesChannel() {
        return penaltiesChannel;
    }

    @NotNull
    private Integer getConnectionRetryCount() {
        return Optional.of(plugin.getConfigManager())
                .map(mainConfigManager -> mainConfigManager.getConfigValue(ConfigurationField.CONNECTION_RETRY_COUNT))
                .map(Integer::valueOf)
                .orElseGet(() -> Integer.valueOf(ConfigurationField.CONNECTION_RETRY_COUNT.getDefaultValue()));
    }


    private void obtainChannelToSendPenalties() {
        for (int i = connectionRetryCount; i >= 0; i--) {
            this.penaltiesChannel = discordSRV.getJda().getTextChannelsByName(channelToSendMessages, false)
                    .stream()
                    .findAny().orElse(null);
            if (penaltiesChannel != null) {
                LOGGER.debug("Channel to send messages successfully obtained, channel name: {}", channelToSendMessages);
                discordMessageSender = new DiscordMessageSender(plugin, getPenaltiesChannel());
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void obtainAccountLinkManager() {
        for (int i = connectionRetryCount; i >= 0; i--) {
            this.accountLinkManager = discordSRV.getAccountLinkManager();
            if (accountLinkManager != null) {
                LOGGER.debug("Account link manager successfully obtained");
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
