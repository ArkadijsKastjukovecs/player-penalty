package akc.plugin.playerpenalty.domain;

public enum TicketType {

    ISSUE(11997457),
    PARDON(52084);

    private final int ticketColor;

    TicketType(int ticketColor) {
        this.ticketColor = ticketColor;
    }

    public int getTicketColor() {
        return ticketColor;
    }
}
