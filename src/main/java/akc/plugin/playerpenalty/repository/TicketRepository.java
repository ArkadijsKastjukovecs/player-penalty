package akc.plugin.playerpenalty.repository;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.entities.PlayerEntity;
import akc.plugin.playerpenalty.domain.entities.TicketEntity;
import akc.plugin.playerpenalty.manager.DatabaseConnectionManager;
import akc.plugin.playerpenalty.manager.DiscordSRVManager;
import org.bukkit.entity.Player;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class TicketRepository {

    private final DatabaseConnectionManager databaseConnectionManager;
    private final DiscordSRVManager discordSRVManager;

    public TicketRepository(PlayerPenaltyPlugin plugin) {
        this.databaseConnectionManager = plugin.getDatabaseConnectionManager();
        this.discordSRVManager = plugin.getDiscordSRVManager();
    }

    public List<TicketEntity> findOpenIssues(Player player) {
        try (final var session = databaseConnectionManager.getSession()) {
            return getPlayerNullsafe(player, session)
                    .getTargetTickets().stream()
                    .filter(TicketEntity::getShouldBePaid)
                    .toList();
        }
    }

    public List<TicketEntity> findOpenVictimTickets(Player player) {
        try (final var session = databaseConnectionManager.getSession()) {
            return getPlayerNullsafe(player, session)
                    .getVictimTickets().stream()
                    .filter(TicketEntity::getShouldBePaid)
                    .toList();
        }
    }

    public void saveNewTicket(TicketEntity ticket) {
        try (final var session = databaseConnectionManager.getSession()) {
            Optional.ofNullable(ticket.getSchedule())
                    .ifPresent(schedule -> schedule.setSourceTicket(ticket));
            session.save(ticket);
            Optional.ofNullable(ticket.getSchedule())
                    .ifPresent(session::save);
        }

    }

    public void updateExistingTicket(TicketEntity ticket) {
        try (final var session = databaseConnectionManager.getSession()) {
            final var transaction = session.beginTransaction();
            Optional.ofNullable(ticket.getSchedule())
                    .ifPresent(schedule -> schedule.setSourceTicket(ticket));
            session.update(ticket);
            Optional.ofNullable(ticket.getSchedule())
                    .ifPresent(session::update);
            transaction.commit();
        }

    }

    public PlayerEntity getOrCreatePlayer(Player player) {
        try (final var session = databaseConnectionManager.getSession()) {
            return getPlayerNullsafe(player, session);
        }
    }

    private PlayerEntity getPlayerNullsafe(Player player, Session session) {
        final var query = session.createQuery("SELECT p FROM akc.plugin.playerpenalty.domain.entities.PlayerEntity p where p.playerId = :id", PlayerEntity.class);
        query.setParameter("id", player.getUniqueId().toString());
        return query.getResultList().stream()
                .findFirst()
                .orElseGet(createNewPlayer(player, session));
    }

    private Supplier<PlayerEntity> createNewPlayer(Player player, Session session) {
        return () -> {
            final var discordId = discordSRVManager.getDiscordId(player);
            final var playerEntity = new PlayerEntity()
                    .setPlayerId(player.getUniqueId().toString())
                    .setPlayerDiscordId(discordId)
                    .setPlayerName(player.getName());
            session.save(playerEntity);
            return playerEntity;
        };
    }
}
