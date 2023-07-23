package akc.plugin.playerpenalty.commands.transformation;

import java.util.function.Function;

public class SomeValueTransformer implements Function<String, String> {

    @Override
    public String apply(String someValue) {
        return someValue.trim();
    }
}
