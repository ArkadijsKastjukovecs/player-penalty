package akc.plugin.playerpenalty.domain.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedule", indexes = {
        @Index(columnList = "source_ticket_id", name = "index_source_ticket_id"),
        @Index(columnList = "is_active", name = "index_is_active"),
        @Index(columnList = "deadline", name = "index_deadline")
})
public class ScheduledEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "source_ticket_id")
    private TicketEntity sourceTicket;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "bukkit_task_id")
    private Integer bukkitTaskId;

    public Integer getId() {
        return id;
    }

    public ScheduledEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public TicketEntity getSourceTicket() {
        return sourceTicket;
    }

    public ScheduledEntity setSourceTicket(TicketEntity sourceTicket) {
        this.sourceTicket = sourceTicket;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public ScheduledEntity setActive(boolean active) {
        this.active = active;
        return this;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public ScheduledEntity setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
        return this;
    }

    public Integer getBukkitTaskId() {
        return bukkitTaskId;
    }

    public ScheduledEntity setBukkitTaskId(Integer bukkitTaskId) {
        this.bukkitTaskId = bukkitTaskId;
        return this;
    }
}
