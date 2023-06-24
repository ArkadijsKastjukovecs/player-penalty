package akc.plugin.playerpenalty.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCommand implements TabExecutor {

    private static final List<String> DURATION_SUGGESTIONS = List.of("1d", "10m", "5h30m");
    protected final List<SubCommand> subCommands;

    private final String commandName;

    protected AbstractCommand(List<SubCommand> subCommands, String commandName) {
        this.subCommands = subCommands;
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return getSuggestions(subCommands, 0, args);
    }

    private List<String> getSuggestions(List<SubCommand> subCommands, int depth, String[] args) {
        String[] combineStringArgs = combineStringArgs(args);
        if (depth == combineStringArgs.length - 1) {

            final var commandsSuggestions = subCommands.stream()
                    .map(SubCommand::getCommandValue)
                    .collect(Collectors.toList());

            final var anyCommandAllowsPlayer = subCommands.stream().anyMatch(it -> it.getArgumentType().equals(ArgumentType.PLAYER));
            if (anyCommandAllowsPlayer) {
                final var playerNames = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList());
                commandsSuggestions.addAll(playerNames);
            }

            final var anyCommandAllowsDuration = subCommands.stream().anyMatch(it -> it.getArgumentType().equals(ArgumentType.DURATION));
            if (anyCommandAllowsDuration) {
                commandsSuggestions.addAll(DURATION_SUGGESTIONS);
            }
            return commandsSuggestions;
        }
        int finalDepth = depth;
        final var subCommandOpt = subCommands.stream()
                .filter(it -> commandIsTyped(it, combineStringArgs[finalDepth]))
                .findFirst();
        if (subCommandOpt.isPresent()) {
            final var subCommand = subCommandOpt.get();
            return getSuggestions(subCommand.getSubCommands(), ++depth, combineStringArgs);
        }
        return Collections.emptyList();
    }

    protected String[] combineStringArgs(String[] args) {
        final var argsToReturn = new ArrayList<String>();
        boolean insideQuotes = false;
        StringBuilder oneParamBuilder = new StringBuilder();
        for (String arg : args) {
            if (!oneParamBuilder.isEmpty()) {
                oneParamBuilder.append(" ");
            }

            if (arg.startsWith("\"")) {
                insideQuotes = true;
                oneParamBuilder = new StringBuilder();
            }

            if (insideQuotes) {
                oneParamBuilder.append(arg.replaceAll("\"", ""));
            }

            if (arg.endsWith("\"")) {
                insideQuotes = false;
                argsToReturn.add(oneParamBuilder.toString());
            } else if (!insideQuotes) {
                argsToReturn.add(arg);
            }
        }

        if (insideQuotes) {
            argsToReturn.add(oneParamBuilder.toString());
        }

        return argsToReturn.toArray(new String[0]);
    }

    private boolean commandIsTyped(SubCommand subCommand, String typedValue) {
        final var argumentType = subCommand.getArgumentType();
        if (argumentType.equals(ArgumentType.COMMAND)) {
            return typedValue.equals(subCommand.getCommandValue());
        }
        return true;
    }
}
