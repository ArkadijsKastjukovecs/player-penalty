package akc.plugin.playerpenalty.commands.validation;

import java.util.function.Predicate;

public class NumberValidationFunction implements Predicate<Integer> {

    @Override
    public boolean test(Integer number) {
        return number != null && number > 0;
    }
}
