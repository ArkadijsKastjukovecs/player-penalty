package akc.plugin.playerpenalty.domain.entities;


import akc.plugin.playerpenalty.domain.TicketType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ticket", indexes = @Index(columnList = "value1", name = "index_value"))
public class TicketEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column("source_ticket")
    private TicketEntity sourceTicket;

    @Enumerated(EnumType.STRING)
    @Column("ticket_type")
    private TicketType ticketType;

    @Column("target_player")
    @ManyToOne(targetEntity = PlayerEntity.class)
    private PlayerEntity targetPlayer;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public TicketEntity setValue(String value) {
        this.value = value;
        return this;
    }
}
