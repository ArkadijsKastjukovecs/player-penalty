package akc.plugin.playerpenalty.commands.validation;

import java.util.function.Predicate;

public class SomeValueValidationFunction implements Predicate<String> {

    @Override
    public boolean test(String someValue) {
        return !someValue.isBlank();
    }
}
