package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.commands.validation.DurationValidationFunction;
import akc.plugin.playerpenalty.commands.validation.NumberValidationFunction;
import akc.plugin.playerpenalty.commands.validation.PlayerValidationFunction;
import akc.plugin.playerpenalty.commands.validation.SomeValueValidationFunction;
import akc.plugin.playerpenalty.commands.validation.TicketNumberValidationFunction;

public class ValidationManager {

    private final PlayerValidationFunction playerValidator;
    private final NumberValidationFunction numberValidationFunction;
    private final DurationValidationFunction durationValidationFunction;
    private final SomeValueValidationFunction someValueValidationFunction;
    private final TicketNumberValidationFunction ticketNumberValidationFunction;

    public ValidationManager() {
        this.playerValidator = new PlayerValidationFunction();
        this.numberValidationFunction = new NumberValidationFunction();
        this.durationValidationFunction = new DurationValidationFunction();
        this.someValueValidationFunction = new SomeValueValidationFunction();
        this.ticketNumberValidationFunction = new TicketNumberValidationFunction();
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
