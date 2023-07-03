package akc.plugin.playerpenalty.handlers;

import akc.plugin.playerpenalty.domain.Ticket;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ScheduledTaskHandler {

    private final Map<Ticket, BukkitTask> ticketTaskMap = new HashMap<>();

    public void scheduleTask(Ticket ticket, BukkitTask task) {
        if (ticketTaskMap.containsKey(ticket)) {
            ticketTaskMap.get(ticket).cancel();
        }

        ticketTaskMap.put(ticket, task);
    }

    public void cancelTask(Ticket ticket) {
        Optional.ofNullable(ticketTaskMap.get(ticket))
                .ifPresent(BukkitTask::cancel);
        ticketTaskMap.remove(ticket);
    }
}
