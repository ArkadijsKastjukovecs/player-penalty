package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.ConfigurationFields;
import akc.plugin.playerpenalty.domain.ArgumentType;
import akc.plugin.playerpenalty.domain.Ticket;
import akc.plugin.playerpenalty.manager.TicketManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractCommand implements TabExecutor {

    private static final List<String> DURATION_SUGGESTIONS = List.of("1d", "10m", "5h30m", "40s", "2w");
    protected final List<SubCommand> subCommands;
    protected final PlayerPenaltyPlugin plugin;
    protected final TicketManager ticketManager;

    private final String currentZone;
    private final String commandName;

    protected AbstractCommand(List<SubCommand> subCommands, PlayerPenaltyPlugin plugin, String commandName) {
        this.subCommands = subCommands;
        this.plugin = plugin;
        this.commandName = commandName;
        this.currentZone = plugin.getConfigManager().getConfigValue(ConfigurationFields.CURRENT_ZONE_ID);
        this.ticketManager = plugin.getTicketManager();
    }

    public String getCommandName() {
        return commandName;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return getSuggestions(subCommands, 0, args, sender);
    }

    private List<String> getSuggestions(List<SubCommand> subCommands, int depth, String[] args, @NotNull CommandSender sender) {
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

            final var anyCommandAllowsTicketNumber = subCommands.stream().anyMatch(it -> it.getArgumentType().equals(ArgumentType.TICKET_NUMBER));
            if (anyCommandAllowsTicketNumber && sender instanceof Player player) {
                final var notPaidIssues = ticketManager.findOpenIssues(player).stream()
                        .map(Ticket::getTicketNumber)
                        .collect(Collectors.toList());

                commandsSuggestions.addAll(notPaidIssues);
            }

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

    protected LocalDateTime getTimeFromDuration(String duration) {
        var currentTime = LocalDateTime.now(ZoneId.of(currentZone));

        for (String adjustment : duration.split("(?<=[wmsdh])(?=\\d)")) {
            final var adjustmentFunc = toLocalDateAdjustment.apply(adjustment);
            currentTime = adjustmentFunc.apply(currentTime);
        }

        return currentTime;
    }

    private Function<String, Function<LocalDateTime, LocalDateTime>> toLocalDateAdjustment = arg -> {
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
        return Function.identity();
    };
}
