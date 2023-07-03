package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.commands.validation.*;

public class ValidationManager {

    private final PlayerValidationFunction playerValidator;
    private final NumberValidationFunction numberValidationFunction;
    private final DurationValidationFunction durationValidationFunction;
    private final SomeValueValidationFunction someValueValidationFunction;
    private final TicketNumberValidationFunction ticketNumberValidationFunction;


    public ValidationManager(PlayerPenaltyPlugin plugin) {
        this.playerValidator = new PlayerValidationFunction();
        this.numberValidationFunction = new NumberValidationFunction();
        this.durationValidationFunction = new DurationValidationFunction();
        this.someValueValidationFunction = new SomeValueValidationFunction();
        this.ticketNumberValidationFunction = new TicketNumberValidationFunction(plugin);
    }

    public PlayerValidationFunction getPlayerValidator() {
        return playerValidator;
    }

    public NumberValidationFunction getNumberValidationFunction() {
        return numberValidationFunction;
    }

    public DurationValidationFunction getDurationValidationFunction() {
        return durationValidationFunction;
    }

    public SomeValueValidationFunction getSomeValueValidationFunction() {
        return someValueValidationFunction;
    }

    public TicketNumberValidationFunction getTicketNumberValidationFunction() {
        return ticketNumberValidationFunction;
    }
}
