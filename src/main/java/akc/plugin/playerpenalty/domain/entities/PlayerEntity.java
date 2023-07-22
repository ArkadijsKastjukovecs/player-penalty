package akc.plugin.playerpenalty.domain.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "player")
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "player_id")
    private UUID playerId;

    @Column(name = "player_discord_id")
    private String playerDiscordId;

    @Column(name = "player_name")
    private String playerName;

    @OneToMany(mappedBy = "targetPlayer", fetch = FetchType.EAGER)
    private List<TicketEntity> targetTickets = new ArrayList<>();

    @OneToMany(mappedBy = "policePlayer", fetch = FetchType.EAGER)
    private List<TicketEntity> policeTickets = new ArrayList<>();

    @OneToMany(mappedBy = "victim", fetch = FetchType.EAGER)
    private List<TicketEntity> victimTickets = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public PlayerEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public PlayerEntity setPlayerId(UUID playerId) {
        this.playerId = playerId;
        return this;
    }

    public String getPlayerDiscordId() {
        return playerDiscordId;
    }

    public PlayerEntity setPlayerDiscordId(String playerDiscordId) {
        this.playerDiscordId = playerDiscordId;
        return this;
    }

    public List<TicketEntity> getTargetTickets() {
        return targetTickets;
    }

    public PlayerEntity setTargetTickets(List<TicketEntity> targetTickets) {
        this.targetTickets = targetTickets;
        return this;
    }

    public List<TicketEntity> getPoliceTickets() {
        return policeTickets;
    }

    public PlayerEntity setPoliceTickets(List<TicketEntity> policeTickets) {
        this.policeTickets = policeTickets;
        return this;
    }

    public List<TicketEntity> getVictimTickets() {
        return victimTickets;
    }

    public PlayerEntity setVictimTickets(List<TicketEntity> victimTickets) {
        this.victimTickets = victimTickets;
        return this;
    }

    public String getPlayerName() {
        return playerName;
    }

    public PlayerEntity setPlayerName(String playerName) {
        this.playerName = playerName;
        return this;
    }
}
