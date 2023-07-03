package akc.plugin.playerpenalty.domain;

import java.util.EnumSet;

public enum TicketType {

    ISSUE(11997457),
    DOUBLE_ISSUE(11997457),
    PARDON(52084),
    FORGIVE(39129);

    private final int ticketColor;
    private static final EnumSet<TicketType> issues = EnumSet.of(ISSUE, DOUBLE_ISSUE);

    TicketType(int ticketColor) {
        this.ticketColor = ticketColor;
    }

    public int getTicketColor() {
        return ticketColor;
    }

    public static boolean isIssue(TicketType ticketType) {
        return issues.contains(ticketType);
    }
}
