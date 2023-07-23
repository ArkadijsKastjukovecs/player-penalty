package akc.plugin.playerpenalty.domain.entities;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "player")
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "player_id")
    private String playerId;

    @Column(name = "player_discord_id")
    private String playerDiscordId;

    @Column(name = "player_name")
    private String playerName;

    @OneToMany(mappedBy = "targetPlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<TicketEntity> targetTickets = new HashSet<>();

    @OneToMany(mappedBy = "policePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<TicketEntity> policeTickets = new HashSet<>();

    @OneToMany(mappedBy = "victim", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<TicketEntity> victimTickets = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public PlayerEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getPlayerId() {
        return playerId;
    }

    public PlayerEntity setPlayerId(String playerId) {
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

    public Set<TicketEntity> getTargetTickets() {
        return targetTickets;
    }

    public PlayerEntity setTargetTickets(Set<TicketEntity> targetTickets) {
        this.targetTickets = targetTickets;
        return this;
    }

    public Set<TicketEntity> getPoliceTickets() {
        return policeTickets;
    }

    public PlayerEntity setPoliceTickets(Set<TicketEntity> policeTickets) {
        this.policeTickets = policeTickets;
        return this;
    }

    public Set<TicketEntity> getVictimTickets() {
        return victimTickets;
    }

    public PlayerEntity setVictimTickets(Set<TicketEntity> victimTickets) {
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
