package akc.plugin.playerpenalty.commands.validation;

import akc.plugin.playerpenalty.domain.entities.TicketEntity;

import java.util.function.Predicate;

public class TicketNumberValidationFunction implements Predicate<TicketEntity> {

    @Override
    public boolean test(TicketEntity ticket) {
        return ticket != null && ticket.getShouldBePaid();
    }
}
