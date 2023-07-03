package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.ConfigurationFields;
import akc.plugin.playerpenalty.domain.ArgumentType;
import akc.plugin.playerpenalty.domain.Ticket;
import akc.plugin.playerpenalty.domain.TicketType;
import akc.plugin.playerpenalty.handlers.ScheduledTaskHandler;
import akc.plugin.playerpenalty.manager.DiscordSRVManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CreateIssueCommand extends AbstractCommand {

    private static final int TICK_IN_ONE_SECOND = 20;
    private final DiscordSRVManager discordSRVManager;
    private final String zoneId;
    private final ScheduledTaskHandler scheduledTaskHandler;

    public CreateIssueCommand(PlayerPenaltyPlugin plugin) {
        super(new ArrayList<>(), plugin, "createIssue", List.of(Player.class));
        zoneId = plugin.getConfigManager().getConfigValue(ConfigurationFields.CURRENT_ZONE_ID);
        this.subCommands.add(createSubCommand());
        this.discordSRVManager = plugin.getDiscordSRVManager();
        this.scheduledTaskHandler = plugin.getScheduledTaskHandler();
    }

    @Override
    protected final boolean handleCommand(CommandSender sender, Ticket ticket, String[] args) {
        var player = (Player) sender;

        final var targetPlayer = ticket.getTargetPlayer();
        final var discordId = discordSRVManager.getDiscordId(targetPlayer);
        ticket.setPolicePlayer(player)
                .setTicketType(TicketType.ISSUE)
                .setTargetPlayerDiscordId(discordId);

        discordSRVManager.sendMEssageToDiscord(ticket);
        ticketManager.addTicketToPlayer(targetPlayer, ticket);
        scheduleRepeatTask(ticket);
        return false;
    }

    private void scheduleRepeatTask(Ticket ticket) {
        final var secondsToDeadline = ChronoUnit.SECONDS.between(LocalDateTime.now(ZoneId.of(zoneId)), ticket.getDeadline());
        final var doubledTicket = new Ticket();
        final var bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    ticket.copyTo(doubledTicket);
                    doubledTicket.setPenaltyAmount(ticket.getPenaltyAmount() * 2)
                            .setTicketType(TicketType.DOUBLE_ISSUE);

                    ticket.setResolved(true);
                    discordSRVManager.sendMEssageToDiscord(doubledTicket);
                    ticketManager.addTicketToPlayer(ticket.getTargetPlayer(), doubledTicket);
                },
                secondsToDeadline * TICK_IN_ONE_SECOND);
        scheduledTaskHandler.scheduleTask(ticket, bukkitTask);
    }

    private SubCommand<?> createSubCommand() {
        return SubCommand.<Player>builder()
                .commandValue("Преступник")
                .argumentType(ArgumentType.PLAYER)
                .buildAppender(Ticket::setTargetPlayer)
                .valueTransformer(transformerManager.getPlayerTransformer())
                .validationFunction(validationManager.getPlayerValidator())
                .required(true)
                .subCommands(List.of(SubCommand.<Player>builder()
                        .commandValue("Жертва")
                        .argumentType(ArgumentType.PLAYER)
                        .buildAppender(Ticket::setVictim)
                        .valueTransformer(transformerManager.getPlayerTransformer())
                        .validationFunction(validationManager.getPlayerValidator())
                        .required(true)
                        .subCommands(List.of(SubCommand.<Integer>builder()
                                .commandValue("количество")
                                .argumentType(ArgumentType.NUMBER)
                                .buildAppender(Ticket::setPenaltyAmount)
                                .validationFunction(validationManager.getNumberValidationFunction())
                                .valueTransformer(transformerManager.getNumberTransformer())
                                .required(true)
                                .subCommands(List.of(SubCommand.<LocalDateTime>builder()
                                        .commandValue("Длительность")
                                        .argumentType(ArgumentType.DURATION)
                                        .buildAppender(Ticket::setDeadline)
                                        .validationFunction(validationManager.getDurationValidationFunction())
                                        .valueTransformer(transformerManager.getDurationTransformer())
                                        .required(true)
                                        .subCommands(List.of(SubCommand.<String>builder()
                                                .commandValue("Причина")
                                                .argumentType(ArgumentType.SOME_VALUE)
                                                .buildAppender(Ticket::setReason)
                                                .valueTransformer(transformerManager.getSomeValueTransformer())
                                                .validationFunction(validationManager.getSomeValueValidationFunction())
                                                .required(true)
                                                .build()))
                                        .build()))
                                .build()))
                        .build()))
                .build();
    }
}
