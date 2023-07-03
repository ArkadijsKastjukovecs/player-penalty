package akc.plugin.playerpenalty.commands.validation;

import java.time.LocalDateTime;
import java.util.function.Predicate;

public class DurationValidationFunction implements Predicate<LocalDateTime> {

    @Override
    public boolean test(LocalDateTime time) {
        return time != null;
    }
}
