package akc.plugin.playerpenalty.commands.transformation;

import java.util.Optional;
import java.util.function.Function;

public class NumberTransformer implements Function<String, Integer> {

    @Override
    public Integer apply(String number) {
        return Optional.ofNullable(number)
                .filter(num -> num.replaceAll("\\d+", "").isBlank())
                .map(String::trim)
                .map(Integer::parseInt)
                .orElse(null);
    }
}
