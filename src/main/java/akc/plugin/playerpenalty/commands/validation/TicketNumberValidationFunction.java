package akc.plugin.playerpenalty.commands.validation;

import akc.plugin.playerpenalty.domain.Ticket;

import java.util.function.Predicate;

public class TicketNumberValidationFunction implements Predicate<Ticket> {

    @Override
    public boolean test(Ticket ticket) {
        return ticket != null;
    }
}
