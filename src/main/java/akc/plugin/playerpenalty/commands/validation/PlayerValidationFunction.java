package akc.plugin.playerpenalty.commands.validation;

import akc.plugin.playerpenalty.domain.entities.PlayerEntity;

import java.util.function.Predicate;

public class PlayerValidationFunction implements Predicate<PlayerEntity> {

    @Override
    public boolean test(PlayerEntity player) {
        return player != null;
    }
}
