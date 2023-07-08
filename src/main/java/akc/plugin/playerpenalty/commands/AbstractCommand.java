package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.ArgumentType;
import akc.plugin.playerpenalty.domain.Ticket;
import akc.plugin.playerpenalty.manager.TicketManager;
import akc.plugin.playerpenalty.manager.TransformerManager;
import akc.plugin.playerpenalty.manager.ValidationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"java:S3740", "unchecked"})
public abstract class AbstractCommand implements TabExecutor {

    private static final List<String> DURATION_SUGGESTIONS = List.of("1d", "10m", "5h30m", "40s", "2w");
    private static final List<String> NUMBER_SUGGESTIONS = List.of("1", "10", "100", "1000");
    private final List<SubCommand<?>> subCommands = new ArrayList<>();
    protected final PlayerPenaltyPlugin plugin;
    protected final TicketManager ticketManager;
    protected final ValidationManager validationManager;
    protected final TransformerManager transformerManager;

    private final String commandName;
    private final List<Class<?>> allowedSenders;

    protected AbstractCommand(PlayerPenaltyPlugin plugin, String commandName, List<Class<?>> allowedSenders) {
        this.plugin = plugin;
        this.commandName = commandName;
        this.ticketManager = plugin.getTicketManager();
        this.validationManager = plugin.getValidationManager();
        this.transformerManager = plugin.getTransformerManager();
        this.allowedSenders = allowedSenders;
        subCommands.addAll(createSubCommands());
    }

    @SuppressWarnings("java:S1452")
    protected abstract List<SubCommand<?>> createSubCommands();

    protected abstract boolean handleCommand(CommandSender sender, Ticket ticketBuilder, String[] args);

    public String getCommandName() {
        return commandName;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final var isSenderAllowed = allowedSenders.stream()
                .anyMatch(it -> it.isInstance(sender));
        if (!isSenderAllowed) {
            sender.sendMessage("эту комманду нельзя использовать через " + sender.getName());
        }

        final var combinedStringArgs = combineStringArgs(args);
        final var ticketBuilder = new Ticket();
        final var requestIsValid = validateAndBuild(subCommands.stream().map(SubCommand.class::cast).toList(), combinedStringArgs, ticketBuilder, 0, sender);
        if (!requestIsValid) {
            return true;
        }

        return handleCommand(sender, ticketBuilder, combinedStringArgs);
    }

    private boolean validateAndBuild(List<SubCommand> commands, String[] args, Ticket ticketBuilder, int currentArg, CommandSender sender) {
        if (commands.isEmpty()) {
            return true;
        }

        int finalCurrentArg = currentArg;
        final var subCommandOpt = commands.stream()
                .filter(subCommand -> {
                    final var transformedValue = subCommand.getArgumentType().equals(ArgumentType.TICKET_NUMBER) ?
                            subCommand.getPlayerValueTransformer().apply(args[finalCurrentArg], sender) :
                            subCommand.getValueTransformer().apply(args[finalCurrentArg]);
                    final var isCommandValid = subCommand.getValidationFunction().test(transformedValue);
                    if (isCommandValid) {
                        subCommand.getBuildAppender().apply(ticketBuilder, transformedValue);
                    }
                    return isCommandValid;
                })
                .findFirst();

        if (subCommandOpt.isEmpty()) {
            sender.sendMessage("Недопустимое значение: " + args[currentArg]);
            return false;
        }
        return validateAndBuild(subCommandOpt.get().getSubCommands(), args, ticketBuilder, ++currentArg, sender);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (allowedSenders.stream().noneMatch(allowedSender -> allowedSender.isInstance(sender))) {
            return null;
        }
        return getSuggestions(subCommands, 0, args, sender);
    }

    private List<String> getSuggestions(List<SubCommand<?>> subCommands, int depth, String[] args, @NotNull CommandSender sender) {
        String[] combineStringArgs = combineStringArgs(args);
        if (depth == combineStringArgs.length - 1) {

            final var commandsSuggestions = subCommands.stream()
                    .map(SubCommand::getCommandValue)
                    .collect(Collectors.toList());

            final var anyCommandAllowsPlayer = subCommands.stream().anyMatch(it -> it.getArgumentType().equals(ArgumentType.PLAYER));
            if (anyCommandAllowsPlayer) {
                final var playerNames = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .toList();
                commandsSuggestions.addAll(playerNames);
            }

            final var anyCommandAllowsDuration = subCommands.stream().anyMatch(it -> it.getArgumentType().equals(ArgumentType.DURATION));
            if (anyCommandAllowsDuration) {
                commandsSuggestions.addAll(DURATION_SUGGESTIONS);
            }

            final var anyCommandAllowsNumber = subCommands.stream().anyMatch(it -> it.getArgumentType().equals(ArgumentType.NUMBER));
            if (anyCommandAllowsNumber) {
                commandsSuggestions.addAll(NUMBER_SUGGESTIONS);
            }

            subCommands.stream()
                    .map(SubCommand::getCustomSuggestionProvider)
                    .filter(Objects::nonNull)
                    .map(it -> it.apply((Player) sender))
                    .flatMap(Collection::stream)
                    .map(String.class::cast)
                    .forEach(commandsSuggestions::add);

            return commandsSuggestions;
        }
        int finalDepth = depth;
        final var subCommandOpt = subCommands.stream()
                .filter(it -> commandIsTyped(it, combineStringArgs[finalDepth]))
                .findFirst();
        if (subCommandOpt.isPresent()) {
            final var subCommand = subCommandOpt.get();
            return getSuggestions(subCommand.getSubCommands(), ++depth, combineStringArgs, sender);
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
                oneParamBuilder.append(arg.replace("\"", ""));
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

    private boolean commandIsTyped(SubCommand<?> subCommand, String typedValue) {
        final var argumentType = subCommand.getArgumentType();
        if (argumentType.equals(ArgumentType.COMMAND)) {
            return typedValue.equals(subCommand.getCommandValue());
        }
        return true;
    }

}
