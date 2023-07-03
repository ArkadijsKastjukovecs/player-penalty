package akc.plugin.playerpenalty.commands.validation;

import org.bukkit.entity.Player;

import java.util.function.Predicate;

public class PlayerValidationFunction implements Predicate<Player> {

    @Override
    public boolean test(Player player) {
        return player != null;
    }
}
