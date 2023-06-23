package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.Ticket;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CreateIssueCommand extends AbstractCommand {

    private final PlayerPenaltyPlugin plugin;
    private final int minRequiredLenght;

    public CreateIssueCommand(PlayerPenaltyPlugin plugin) {
        super(List.of(createSubCommand()), "createIssue");
        this.plugin = plugin;
        this.minRequiredLenght = 5;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player police) {
            if (args.length < minRequiredLenght) {
                sender.sendMessage("Все параметры должны быть указаны");
                return true;
            }

            final var invalidArgument = validateArgs(args);
            if (invalidArgument != null) {
                sender.sendMessage("не удалось распознать параметр %s".formatted(invalidArgument));
                return true;
            }

            final var build = Ticket.builder()
                    .policePlayer(police)
                    .targetPlayer(getPlayerOrOfflinePlayer(args[0]))
                    .victim(getPlayerOrOfflinePlayer(args[1]))
                    .penaltyAmount(Integer.valueOf(args[2]))
                    .reason(args[3])
                    .deadline()
                    .build();

        } else {
            sender.sendMessage("Только игроки могут отправлять эту комманду");
            return true;
        }
        return true;
    }

    private LocalDateTime getTimeFromDuration(String duration) {
        final var currentTime = LocalDateTime.now();
        Arrays.stream(duration.split("[wsmdh]]"))
                .map(toLocalDateAdjustment)
//                .reduce(LocalDateTime.now(), ((localDateTime, localDateTimeLocalDateTimeFunction) -> localDateTime.e(localDateTimeLocalDateTimeFunction)) )
//                .map(func -> func.apply(currentTime));
//        currentTime.pl
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
    }

    private String validateArgs(String[] args) {
        // TODO
        return null;
    }

    private Player getPlayerOrOfflinePlayer(String playerName) {
        return Optional.ofNullable(Bukkit.getPlayer(playerName))
                .orElseGet(() -> Bukkit.getOfflinePlayer(playerName).getPlayer());
    }

    private static SubCommand createSubCommand() {
        return SubCommand.builder()
                .commandValue("Преступник")
                .argumentType(ArgumentType.PLAYER)
                .required(true)
                .subCommands(List.of(SubCommand.builder()
                        .commandValue("Жертва")
                        .argumentType(ArgumentType.PLAYER)
                        .required(true)
                        .subCommands(List.of(SubCommand.builder()
                                .commandValue("количество")
                                .argumentType(ArgumentType.SOME_VALUE)
                                .required(true)
                                .subCommands(List.of(SubCommand.builder()
                                        .commandValue("Причина")
                                        .argumentType(ArgumentType.SOME_VALUE)
                                        .required(true)
                                        .subCommands(List.of(SubCommand.builder()
                                                .commandValue("Длительность")
                                                .required(true)
                                                .argumentType(ArgumentType.DURATION)
                                                .build()))
                                        .build()))
                                .build()))
                        .build()))
                .build();
    }
}
