package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.domain.ArgumentType;
import akc.plugin.playerpenalty.domain.Ticket;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class SubCommand<T> {

    private final String commandValue;
    private final ArgumentType argumentType;
    private final List<SubCommand<?>> subCommands;
    private final BiFunction<Ticket, T, Ticket> buildAppender;
    private final Predicate<T> validationFunction;
    private final Function<String, T> valueTransformer;
    private final BiFunction<String, Player, T> playerValueTransformer;
    private final Function<Player, List<String>> customSuggestionProvider;
    private final boolean required;

    private SubCommand(Builder<T> b) {
        commandValue = b.commandValue;
        argumentType = b.argumentType;
        subCommands = b.subCommands;
        buildAppender = b.buildAppender;
        validationFunction = b.validationFunction;
        valueTransformer = b.valueTransformer;
        playerValueTransformer = b.playerValueTransformer;
        customSuggestionProvider = b.customSuggestionProvider;
        required = b.required;
    }

    public String getCommandValue() {
        return commandValue;
    }

    public ArgumentType getArgumentType() {
        return argumentType;
    }

    public List<SubCommand<?>> getSubCommands() {
        return subCommands;
    }

    public BiFunction<Ticket, T, Ticket> getBuildAppender() {
        return buildAppender;
    }

    public Predicate<T> getValidationFunction() {
        return validationFunction;
    }

    public Function<String, T> getValueTransformer() {
        return valueTransformer;
    }

    public BiFunction<String, Player, T> getPlayerValueTransformer() {
        return playerValueTransformer;
    }

    public Function<Player, List<String>> getCustomSuggestionProvider() {
        return customSuggestionProvider;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {
        private String commandValue;
        private ArgumentType argumentType;
        private List<SubCommand<?>> subCommands = new ArrayList<>();
        private BiFunction<Ticket, T, Ticket> buildAppender;
        private Predicate<T> validationFunction;
        private Function<String, T> valueTransformer;
        private BiFunction<String, Player, T> playerValueTransformer;
        private Function<Player, List<String>> customSuggestionProvider;
        private boolean required = false;


        private Builder() {
        }

        public Builder<T> commandValue(String commandValue) {
            this.commandValue = commandValue;
            return this;
        }

        public Builder<T> argumentType(ArgumentType argumentType) {
            this.argumentType = argumentType;
            return this;
        }

        public Builder<T> subCommands(List<SubCommand<?>> subCommands) {
            this.subCommands = subCommands;
            return this;
        }

        public Builder<T> required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder<T> buildAppender(BiFunction<Ticket, T, Ticket> buildAppender) {
            this.buildAppender = buildAppender;
            return this;
        }

        public Builder<T> validationFunction(Predicate<T> validationFunction) {
            this.validationFunction = validationFunction;
            return this;
        }

        public Builder<T> valueTransformer(Function<String, T> valueTransformer) {
            this.valueTransformer = valueTransformer;
            return this;
        }

        public Builder<T> playerValueTransformer(BiFunction<String, Player, T> playerValueTransformer) {
            this.playerValueTransformer = playerValueTransformer;
            return this;
        }

        public Builder<T> customSuggestionProvider(Function<Player, List<String>> customSuggestionProvider) {
            this.customSuggestionProvider = customSuggestionProvider;
            return this;
        }

        public SubCommand<T> build() {
            return new SubCommand<T>(this);
        }
    }
}
