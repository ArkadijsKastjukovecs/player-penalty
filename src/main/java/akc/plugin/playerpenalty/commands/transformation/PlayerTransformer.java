package akc.plugin.playerpenalty.commands.transformation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.Function;

public class PlayerTransformer implements Function<String, Player> {

    @Override
    public Player apply(String playerName) {
        return Optional.ofNullable(Bukkit.getPlayer(playerName))
                .orElseGet(() -> Bukkit.getOfflinePlayer(playerName).getPlayer());
    }
}
