package akc.plugin.playerpenalty.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCommand implements TabExecutor {

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
        if (depth == args.length - 1) {

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
            return commandsSuggestions;
        }
        int finalDepth = depth;
        final var subCommandOpt = subCommands.stream()
                .filter(it -> commandIsTyped(it, args[finalDepth]))
                .findFirst();
        if (subCommandOpt.isPresent()) {
            final var subCommand = subCommandOpt.get();
            return getSuggestions(subCommand.getSubCommands(), ++depth, args);
        }
        return Collections.emptyList();
    }

    private boolean commandIsTyped(SubCommand subCommand, String typedValue) {
        final var argumentType = subCommand.getArgumentType();
        if (argumentType.equals(ArgumentType.PLAYER) || argumentType.equals(ArgumentType.SOME_VALUE)) {
            return true;
        }
        return typedValue.equals(subCommand.getCommandValue());
    }
}
