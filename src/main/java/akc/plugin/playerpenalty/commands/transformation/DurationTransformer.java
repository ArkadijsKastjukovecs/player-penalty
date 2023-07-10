package akc.plugin.playerpenalty.commands.transformation;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.ConfigurationField;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class DurationTransformer implements Function<String, LocalDateTime> {

    public static final String ALLOWED_TIME_ADJUSTERS = "wmsdh";

    private static final String ALLOWED_VALUE_SPLITTING_REGEX = "(?<=[" + ALLOWED_TIME_ADJUSTERS + "])(?=\\d)";
    private final String currentZone;

    public DurationTransformer(PlayerPenaltyPlugin plugin) {
        this.currentZone = plugin.getConfigManager().getConfigValue(ConfigurationField.CURRENT_ZONE_ID);
    }

    @Override
    public LocalDateTime apply(String duration) {
        var currentTime = LocalDateTime.now(ZoneId.of(currentZone));

        try {
            for (String adjustment : duration.split(ALLOWED_VALUE_SPLITTING_REGEX)) {
                final var adjustmentFunc = toLocalDateAdjustment.apply(adjustment);
                currentTime = adjustmentFunc.apply(currentTime);
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return currentTime;
    }

    private final Function<String, UnaryOperator<LocalDateTime>> toLocalDateAdjustment = arg -> {
        if (arg.contains("w")) {
            return localDateTime -> localDateTime.plusWeeks(Long.parseLong(arg.substring(0, arg.length() - 1)));
        }
        if (arg.contains("s")) {
            return localDateTime -> localDateTime.plusSeconds(Long.parseLong(arg.substring(0, arg.length() - 1)));
        }
        if (arg.contains("m")) {
            return localDateTime -> localDateTime.plusMinutes(Long.parseLong(arg.substring(0, arg.length() - 1)));
        }
        if (arg.contains("d")) {
            return localDateTime -> localDateTime.plusDays(Long.parseLong(arg.substring(0, arg.length() - 1)));
        }
        if (arg.contains("h")) {
            return localDateTime -> localDateTime.plusHours(Long.parseLong(arg.substring(0, arg.length() - 1)));
        }
        return UnaryOperator.identity();
    };
}
