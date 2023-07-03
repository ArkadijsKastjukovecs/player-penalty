package akc.plugin.playerpenalty.domain;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class Ticket {

    private TicketType ticketType;
    private Player targetPlayer;
    private Player policePlayer;
    private String targetPlayerDiscordId;
    private int penaltyAmount;
    private Player victim;
    private String reason;
    private LocalDateTime deadline;
    private boolean resolved;
    private String ticketNumber;


    public TicketType getTicketType() {
        return ticketType;
    }

    public Ticket setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
        return this;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public Ticket setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
        return this;
    }

    public Player getPolicePlayer() {
        return policePlayer;
    }

    public Ticket setPolicePlayer(Player policePlayer) {
        this.policePlayer = policePlayer;
        return this;
    }

    public String getTargetPlayerDiscordId() {
        return targetPlayerDiscordId;
    }

    public Ticket setTargetPlayerDiscordId(String targetPlayerDiscordId) {
        this.targetPlayerDiscordId = targetPlayerDiscordId;
        return this;
    }

    public int getPenaltyAmount() {
        return penaltyAmount;
    }

    public Ticket setPenaltyAmount(int penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
        return this;
    }

    public Player getVictim() {
        return victim;
    }

    public Ticket setVictim(Player victim) {
        this.victim = victim;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public Ticket setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public Ticket setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
        return this;
    }

    public boolean isResolved() {
        return resolved;
    }

    public Ticket setResolved(boolean resolved) {
        this.resolved = resolved;
        return this;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public Ticket setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
        return this;
    }

    public Ticket copyTo(Ticket ticket) {
        return ticket
                .setTicketType(getTicketType())
                .setTargetPlayer(getTargetPlayer())
                .setPolicePlayer(getPolicePlayer())
                .setTargetPlayerDiscordId(getTargetPlayerDiscordId())
                .setPenaltyAmount(getPenaltyAmount())
                .setVictim(getVictim())
                .setReason(getReason())
                .setDeadline(getDeadline())
                .setResolved(isResolved())
                .setTicketNumber(getTicketNumber());
    }
}
