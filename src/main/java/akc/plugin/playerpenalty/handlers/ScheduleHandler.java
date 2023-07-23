package akc.plugin.playerpenalty.handlers;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.ConfigurationField;
import akc.plugin.playerpenalty.domain.TicketType;
import akc.plugin.playerpenalty.domain.entities.TicketEntity;
import akc.plugin.playerpenalty.manager.DiscordSRVManager;
import akc.plugin.playerpenalty.repository.ScheduleRepository;
import akc.plugin.playerpenalty.repository.TicketRepository;
import org.bukkit.Bukkit;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class ScheduleHandler {

    private static final int TICK_IN_ONE_SECOND = 20;

    private final ScheduleRepository scheduleRepository;
    private final TicketRepository ticketRepository;
    private final DiscordSRVManager discordSRVManager;
    private final PlayerPenaltyPlugin plugin;
    private final String zoneId;


    public ScheduleHandler(PlayerPenaltyPlugin plugin) {
        this.scheduleRepository = plugin.getScheduleRepository();
        this.discordSRVManager = plugin.getDiscordSRVManager();
        this.ticketRepository = plugin.getTicketRepository();
        this.zoneId = plugin.getConfigManager().getConfigValue(ConfigurationField.CURRENT_ZONE_ID);
        this.plugin = plugin;
    }

    public void schedulePassedRepeatingTasks() {
        scheduleRepository.getActivePastSchedules().forEach(scheduledEntity -> {
            final var bukkitTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, createDoubleTicket(scheduledEntity.getSourceTicket()));
            scheduledEntity.setBukkitTaskId(bukkitTask.getTaskId());
            scheduleRepository.updateSchedule(scheduledEntity);
        });
    }

    public void scheduleRepeatTask(TicketEntity ticket) {
        final var secondsToDeadline = ChronoUnit.SECONDS.between(LocalDateTime.now(ZoneId.of(zoneId)), ticket.getSchedule().getDeadline());
        final var bukkitTask = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin,
                createDoubleTicket(ticket),
                secondsToDeadline * TICK_IN_ONE_SECOND);
        ticket.getSchedule().setBukkitTaskId(bukkitTask.getTaskId());
    }

    private Runnable createDoubleTicket(TicketEntity ticket) {
        return () -> {
            final var doubledTicket = new TicketEntity();
            ticket.copyTo(doubledTicket);
            ticket.setShouldBePaid(false);
            doubledTicket.setPenaltyAmount(ticket.getPenaltyAmount() * 2)
                    .setTicketType(TicketType.DOUBLE_ISSUE)
                    .setShouldBePaid(true);

            ticket.getSchedule().setActive(false);
            ticketRepository.saveNewTicket(doubledTicket);
            ticketRepository.updateExistingTicket(ticket);
            discordSRVManager.sendMEssageToDiscord(doubledTicket);
        };
    }
}


