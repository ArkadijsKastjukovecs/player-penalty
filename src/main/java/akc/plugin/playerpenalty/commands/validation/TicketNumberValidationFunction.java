package akc.plugin.playerpenalty.commands.validation;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.Ticket;
import akc.plugin.playerpenalty.manager.TicketManager;

import java.util.function.Predicate;

public class TicketNumberValidationFunction implements Predicate<Ticket> {

    private final TicketManager ticketManager;

    public TicketNumberValidationFunction(PlayerPenaltyPlugin plugin) {
        ticketManager = plugin.getTicketManager();
    }

    @Override
    public boolean test(Ticket ticket) {
        return ticket != null && !ticket.isResolved();
    }
}
