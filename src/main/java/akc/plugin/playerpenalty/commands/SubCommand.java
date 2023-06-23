package akc.plugin.playerpenalty.commands;

import java.util.ArrayList;
import java.util.List;

public class SubCommand {

    private final String commandValue;
    private final ArgumentType argumentType;
    private final List<SubCommand> subCommands;
    private final boolean required;

    private SubCommand(Builder b) {
        commandValue = b.commandValue;
        argumentType = b.argumentType;
        subCommands = b.subCommands;
        required = b.required;
    }

    public String getCommandValue() {
        return commandValue;
    }

    public ArgumentType getArgumentType() {
        return argumentType;
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String commandValue;
        private ArgumentType argumentType;
        private List<SubCommand> subCommands = new ArrayList<>();
        private boolean required = false;

        private Builder() {
        }

        public Builder commandValue(String commandValue) {
            this.commandValue = commandValue;
            return this;
        }

        public Builder argumentType(ArgumentType argumentType) {
            this.argumentType = argumentType;
            return this;
        }

        public Builder subCommands(List<SubCommand> subCommands) {
            this.subCommands = subCommands;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public SubCommand build() {
            return new SubCommand(this);
        }
    }
}
