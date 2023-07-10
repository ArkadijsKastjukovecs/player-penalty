package akc.plugin.playerpenalty.repository;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.manager.DatabaseConnectionManager;

public class TicketRepository {

    private final DatabaseConnectionManager databaseConnectionManager;

    public TicketRepository(PlayerPenaltyPlugin plugin) {
        this.databaseConnectionManager = plugin.getDatabaseConnectionManager();
    }


}
