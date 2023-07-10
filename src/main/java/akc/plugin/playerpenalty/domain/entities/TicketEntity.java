package akc.plugin.playerpenalty.domain.entities;


import akc.plugin.playerpenalty.domain.TicketType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ticket", indexes = {
        @Index(columnList = "target_player_id", name = "index_target_player_id")
})
public class TicketEntity {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "source_ticket_id", referencedColumnName = "id")
    private TicketEntity sourceTicket;

//    @OneToOne(mappedBy = "sourceTicket")
//    private TicketEntity sourceTicketRef;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type")
    private TicketType ticketType;

    @JoinColumn(name = "target_player_id")
    @ManyToOne
    private PlayerEntity targetPlayer;

    public Long getId() {
        return id;
    }

    public TicketEntity setId(Long id) {
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

//    public TicketEntity getSourceTicketRef() {
//        return sourceTicketRef;
//    }
//
//    public TicketEntity setSourceTicketRef(TicketEntity sourceTicketRef) {
//        this.sourceTicketRef = sourceTicketRef;
//        return this;
//    }
}
