package akc.plugin.playerpenalty.domain.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(indexes = @Index(columnList = "value", name = "index_value"))
public class TicketEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "value")
    private String value;

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
