package akc.plugin.playerpenalty.commands.transformation;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.entities.TicketEntity;
import akc.plugin.playerpenalty.repository.TicketRepository;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public class TargetPlayerTicketNumberTransformer implements BiFunction<String, Player, TicketEntity> {

    private final TicketRepository ticketRepository;

    public TargetPlayerTicketNumberTransformer(PlayerPenaltyPlugin plugin) {
        this.ticketRepository = plugin.getTicketRepository();
    }

    @Override
    public TicketEntity apply(String ticketNumber, Player player) {
        return ticketRepository.findOpenIssues(player).stream()
                .filter(ticket -> ticket.getId().equals(Integer.valueOf(ticketNumber)))
                .findAny().orElse(null);
    }
}
