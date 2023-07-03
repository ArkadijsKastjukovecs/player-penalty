package akc.plugin.playerpenalty.commands.transformation;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.Ticket;
import akc.plugin.playerpenalty.manager.TicketManager;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public class VictimTicketNumberTransformer implements BiFunction<String, Player, Ticket> {

    private final TicketManager ticketManager;

    public VictimTicketNumberTransformer(PlayerPenaltyPlugin plugin) {
        this.ticketManager = plugin.getTicketManager();
    }

    @Override
    public Ticket apply(String ticketNumber, Player player) {
        return ticketManager.findOpenIssuebyVictim(player).stream()
                .filter(ticket -> ticket.getTicketNumber().equals(ticketNumber))
                .findAny()
                .orElse(null);
    }
}
