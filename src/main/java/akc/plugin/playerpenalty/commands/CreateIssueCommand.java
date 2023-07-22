package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.ConfigurationField;
import akc.plugin.playerpenalty.domain.ArgumentType;
import akc.plugin.playerpenalty.domain.TicketType;
import akc.plugin.playerpenalty.domain.entities.PlayerEntity;
import akc.plugin.playerpenalty.domain.entities.ScheduledEntity;
import akc.plugin.playerpenalty.domain.entities.TicketEntity;
import akc.plugin.playerpenalty.manager.DiscordSRVManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.BiFunction;

public class CreateIssueCommand extends AbstractCommand {

    private static final int TICK_IN_ONE_SECOND = 20;
    private final DiscordSRVManager discordSRVManager;
    private final String zoneId;

    public CreateIssueCommand(PlayerPenaltyPlugin plugin) {
        super(plugin, "createIssue", List.of(Player.class));
        zoneId = plugin.getConfigManager().getConfigValue(ConfigurationField.CURRENT_ZONE_ID);
        this.discordSRVManager = plugin.getDiscordSRVManager();
    }

    @Override
    protected final boolean handleCommand(CommandSender sender, TicketEntity ticket, String[] args) {
        var player = (Player) sender;
        final var senderPlayer = ticketRepository.getOrCreatePlayer(player);

        ticket.setPolicePlayer(senderPlayer)
                .setTicketType(TicketType.ISSUE)
                .setShouldBePaid(true);

        scheduleRepeatTask(ticket);
        ticketRepository.saveNewTicket(ticket);
        discordSRVManager.sendMEssageToDiscord(ticket);
        sender.sendMessage("Штраф под номером %s на сумму %s успешно выписан".formatted(ticket.getId(), ticket.getPenaltyAmount()));
        return false;
    }

    @Override
    protected List<SubCommand<?>> createSubCommands() {
        return List.of(createSubCommand());
    }

    private BiFunction<TicketEntity, LocalDateTime, TicketEntity> addScheduleEntityToTicket() {
        return (ticket, time) -> {
            final var scheduledEntity = new ScheduledEntity()
                    .setActive(true)
                    .setDeadline(time)
                    .setSourceTicket(ticket);
            ticket.setSchedule(scheduledEntity);
            return ticket;
        };
    }

    private void scheduleRepeatTask(TicketEntity ticket) {
        final var secondsToDeadline = ChronoUnit.SECONDS.between(LocalDateTime.now(ZoneId.of(zoneId)), ticket.getSchedule().getDeadline());
        final var doubledTicket = new TicketEntity();
        final var bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    ticket.copyTo(doubledTicket);
                    ticket.setShouldBePaid(false);
                    doubledTicket.setPenaltyAmount(ticket.getPenaltyAmount() * 2)
                            .setTicketType(TicketType.DOUBLE_ISSUE)
                            .setShouldBePaid(true);

                    ticket.getSchedule().setActive(false);
                    ticketRepository.saveNewTicket(doubledTicket);
                    ticketRepository.updateExistingTicket(ticket);
                    discordSRVManager.sendMEssageToDiscord(doubledTicket);
                },
                secondsToDeadline * TICK_IN_ONE_SECOND);
        ticket.getSchedule().setBukkitTaskId(bukkitTask.getTaskId());
    }

    private SubCommand<?> createSubCommand() {
        return SubCommand.<PlayerEntity>builder()
                .commandValue("Преступник")
                .argumentType(ArgumentType.PLAYER)
                .buildAppender(TicketEntity::setTargetPlayer)
                .valueTransformer(transformerManager.getPlayerTransformer())
                .validationFunction(validationManager.getPlayerValidator())
                .subCommands(List.of(SubCommand.<PlayerEntity>builder()
                        .commandValue("Жертва")
                        .argumentType(ArgumentType.PLAYER)
                        .buildAppender(TicketEntity::setVictim)
                        .valueTransformer(transformerManager.getPlayerTransformer())
                        .validationFunction(validationManager.getPlayerValidator())
                        .subCommands(List.of(SubCommand.<Integer>builder()
                                .commandValue("количество")
                                .argumentType(ArgumentType.NUMBER)
                                .buildAppender(TicketEntity::setPenaltyAmount)
                                .validationFunction(validationManager.getNumberValidationFunction())
                                .valueTransformer(transformerManager.getNumberTransformer())
                                .subCommands(List.of(SubCommand.<LocalDateTime>builder()
                                        .commandValue("Длительность")
                                        .argumentType(ArgumentType.DURATION)
                                        .buildAppender(addScheduleEntityToTicket())
                                        .validationFunction(validationManager.getDurationValidationFunction())
                                        .valueTransformer(transformerManager.getDurationTransformer())
                                        .subCommands(List.of(SubCommand.<String>builder()
                                                .commandValue("Причина")
                                                .argumentType(ArgumentType.SOME_VALUE)
                                                .buildAppender(TicketEntity::setReason)
                                                .valueTransformer(transformerManager.getSomeValueTransformer())
                                                .validationFunction(validationManager.getSomeValueValidationFunction())
                                                .build()))
                                        .build()))
                                .build()))
                        .build()))
                .build();
    }
}
