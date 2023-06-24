package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerPointsManager {

    private final PlayerPenaltyPlugin plugin;
    private PlayerPointsAPI pointsAPI;

    public PlayerPointsManager(PlayerPenaltyPlugin plugin) {
        this.plugin = plugin;
    }

    public void initPlayerPointsPlugin() {
        final var playerPoints = JavaPlugin.getPlugin(PlayerPoints.class);
        this.pointsAPI = new PlayerPointsAPI(playerPoints);
    }

    public PlayerPointsAPI getPointsAPI() {
        return pointsAPI;
    }
}
