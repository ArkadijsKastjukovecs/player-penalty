package akc.plugin.playerpenalty.domain;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class Ticket {

    private final TicketType ticketType;
    private final Player targetPlayer;
    private final Player policePlayer;
    private final String targetPlayerDiscordId;
    private final int penaltyAmount;
    private final Player victim;
    private final String reason;
    private final LocalDateTime deadline;

    private boolean paid;
    private String ticketNumber;

    public Builder copyBuilder() {
        return builder()
                .ticketType(this.ticketType)
                .penaltyAmount(this.penaltyAmount)
                .reason(this.reason)
                .targetPlayerDiscordId(this.targetPlayerDiscordId)
                .ticketNumber(this.ticketNumber)
                .victim(this.victim)
                .targetPlayer(this.targetPlayer)
                .policePlayer(this.policePlayer)
                .deadline(this.deadline);
    }

    private Ticket(Builder b) {
        this.ticketType = b.ticketType;
        this.penaltyAmount = b.penaltyAmount;
        this.reason = b.reason;
        this.targetPlayerDiscordId = b.targetPlayerDiscordId;
        this.ticketNumber = b.ticketNumber;
        this.victim = b.victim;
        this.targetPlayer = b.targetPlayer;
        this.policePlayer = b.policePlayer;
        this.deadline = b.deadline;
        this.paid = b.paid;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public String getTargetPlayerDiscordId() {
        return targetPlayerDiscordId;
    }

    public Ticket setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
        return this;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public int getPenaltyAmount() {
        return penaltyAmount;
    }

    public Player getVictim() {
        return victim;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public Player getPolicePlayer() {
        return policePlayer;
    }

    public void markAsPaid() {
        this.paid = true;
    }

    public boolean isPaid() {
        return paid;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private TicketType ticketType;
        private Player targetPlayer;
        private Player policePlayer;
        private String targetPlayerDiscordId;
        private String ticketNumber;
        private int penaltyAmount;
        private Player victim;
        private String reason;
        private LocalDateTime deadline;
        private boolean paid = false;

        private Builder() {
        }

        public Builder ticketType(TicketType ticketType) {
            this.ticketType = ticketType;
            return this;
        }

        public Builder targetPlayer(Player targetPlayer) {
            this.targetPlayer = targetPlayer;
            return this;
        }

        public Builder policePlayer(Player policePlayer) {
            this.policePlayer = policePlayer;
            return this;
        }

        public Builder targetPlayerDiscordId(String targetPlayerDiscordId) {
            this.targetPlayerDiscordId = targetPlayerDiscordId;
            return this;
        }

        public Builder ticketNumber(String ticketNumber) {
            this.ticketNumber = ticketNumber;
            return this;
        }

        public Builder penaltyAmount(int penaltyAmount) {
            this.penaltyAmount = penaltyAmount;
            return this;
        }

        public Builder victim(Player victim) {
            this.victim = victim;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder deadline(LocalDateTime deadline) {
            this.deadline = deadline;
            return this;
        }

        public Builder paid(boolean paid) {
            this.paid = paid;
            return this;
        }

        public Ticket build() {
            return new Ticket(this);
        }
    }
}
