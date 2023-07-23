package akc.plugin.playerpenalty.repository;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.ConfigurationField;
import akc.plugin.playerpenalty.domain.entities.ScheduledEntity;
import akc.plugin.playerpenalty.manager.DatabaseConnectionManager;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

public class ScheduleRepository {

    private final DatabaseConnectionManager databaseConnectionManager;
    private final String zoneId;

    public ScheduleRepository(PlayerPenaltyPlugin plugin) {
        this.databaseConnectionManager = plugin.getDatabaseConnectionManager();
        this.zoneId = plugin.getConfigManager().getConfigValue(ConfigurationField.CURRENT_ZONE_ID);
    }

    public Set<ScheduledEntity> getActivePastSchedules() {
        try (final var session = databaseConnectionManager.getSession()) {
            final var query = session.createQuery("SELECT s FROM akc.plugin.playerpenalty.domain.entities.ScheduledEntity s where s.active is true", ScheduledEntity.class);
            return query.getResultStream()
                    .filter(it -> it.getDeadline().isBefore(LocalDateTime.now(ZoneId.of(zoneId))))
                    .collect(Collectors.toSet());
        }
    }

    public void updateSchedule(ScheduledEntity entity) {
        try (final var session = databaseConnectionManager.getSession()) {
            session.update(entity);
        }
    }
}
