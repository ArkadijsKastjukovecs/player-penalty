package akc.plugin.playerpenalty.domain.entities;


import akc.plugin.playerpenalty.domain.TicketType;
import org.hibernate.annotations.Cascade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ticket", indexes = {
        @Index(columnList = "target_player_id", name = "index_target_player_id"),
        @Index(columnList = "victim_player_id", name = "index_victim_player_id")
})
public class TicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "source_ticket_id", referencedColumnName = "id")
    private TicketEntity sourceTicket;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type")
    private TicketType ticketType;

    @JoinColumn(name = "target_player_id")
    @ManyToOne
    private PlayerEntity targetPlayer;

    @JoinColumn(name = "police_player_id")
    @ManyToOne
    private PlayerEntity policePlayer;

    @JoinColumn(name = "victim_player_id")
    @ManyToOne
    private PlayerEntity victim;

    @Column(name = "penalty_amount")
    private int penaltyAmount;

    @Column(name = "reason", length = 256)
    private String reason;

    @Column(name = "should_be_paid")
    private Boolean shouldBePaid;

    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @OneToOne(mappedBy = "sourceTicket", fetch = FetchType.EAGER)
    private ScheduledEntity schedule;

    public Integer getId() {
        return id;
    }

    public TicketEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public TicketEntity getSourceTicket() {
        return sourceTicket;
    }

    public TicketEntity setSourceTicket(TicketEntity sourceTicket) {
        this.sourceTicket = sourceTicket;
        return this;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public TicketEntity setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
        return this;
    }

    public PlayerEntity getTargetPlayer() {
        return targetPlayer;
    }

    public TicketEntity setTargetPlayer(PlayerEntity targetPlayer) {
        this.targetPlayer = targetPlayer;
        return this;
    }

    public PlayerEntity getPolicePlayer() {
        return policePlayer;
    }

    public TicketEntity setPolicePlayer(PlayerEntity policePlayer) {
        this.policePlayer = policePlayer;
        return this;
    }

    public PlayerEntity getVictim() {
        return victim;
    }

    public TicketEntity setVictim(PlayerEntity victim) {
        this.victim = victim;
        return this;
    }

    public int getPenaltyAmount() {
        return penaltyAmount;
    }

    public TicketEntity setPenaltyAmount(int penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public TicketEntity setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public ScheduledEntity getSchedule() {
        return schedule;
    }

    public TicketEntity setSchedule(ScheduledEntity schedule) {
        this.schedule = schedule;
        return this;
    }

    public Boolean getShouldBePaid() {
        return shouldBePaid;
    }

    public TicketEntity setShouldBePaid(Boolean shouldBePaid) {
        this.shouldBePaid = shouldBePaid;
        return this;
    }

    public TicketEntity copyTo(TicketEntity newTicket) {
        return newTicket
                .setSourceTicket(this)
                .setReason(this.getReason())
                .setTicketType(this.getTicketType())
                .setTargetPlayer(this.getTargetPlayer())
                .setPolicePlayer(this.getPolicePlayer())
                .setPenaltyAmount(this.getPenaltyAmount())
                .setVictim(this.getVictim());
    }
}
