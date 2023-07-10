package akc.plugin.playerpenalty.domain.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "player", indexes = @Index(columnList = "player_unique_id", name = "index_player_unique_id"))
public class PlayerEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "player_unique_id")
    private String playerUUID;

    @Column(name = "player_discord_id")
    private String playerDiscordId;

    @OneToMany(mappedBy = "targetPlayer")
    private List<TicketEntity> sourceTicket;


    public Long getId() {
        return id;
    }

    public PlayerEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public PlayerEntity setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
        return this;
    }

    public String getPlayerDiscordId() {
        return playerDiscordId;
    }

    public PlayerEntity setPlayerDiscordId(String playerDiscordId) {
        this.playerDiscordId = playerDiscordId;
        return this;
    }

    public List<TicketEntity> getSourceTicket() {
        return sourceTicket;
    }

    public PlayerEntity setSourceTicket(List<TicketEntity> sourceTicket) {
        this.sourceTicket = sourceTicket;
        return this;
    }
}
