package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.ArgumentType;
import akc.plugin.playerpenalty.domain.Ticket;
import akc.plugin.playerpenalty.domain.TicketType;
import akc.plugin.playerpenalty.manager.DiscordSRVManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class CreateIssueCommand extends AbstractCommand {

    private final int minRequiredLenght;
    private final DiscordSRVManager discordSRVManager;

    public CreateIssueCommand(PlayerPenaltyPlugin plugin) {
        super(List.of(createSubCommand()), plugin, "createIssue");
        this.minRequiredLenght = 5;
        this.discordSRVManager = plugin.getDiscordSRVManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        String[] combineStringArgs = combineStringArgs(args);

        if (sender instanceof Player police) {
            if (combineStringArgs.length < minRequiredLenght) {
                sender.sendMessage("Все параметры должны быть указаны");
                return true;
            }

            final var invalidArgument = validateArgs(combineStringArgs);
            if (invalidArgument != null) {
                sender.sendMessage("не удалось распознать параметр %s".formatted(invalidArgument));
                return true;
            }
            final var targetPlayer = getPlayerOrOfflinePlayer(combineStringArgs[0]);
            final var targetPlayerDiscordId = discordSRVManager.getDiscordId(targetPlayer);
            final var ticket = Ticket.builder()
                    .policePlayer(police)
                    .targetPlayer(targetPlayer)
                    .victim(getPlayerOrOfflinePlayer(combineStringArgs[1]))
                    .penaltyAmount(Integer.parseInt(combineStringArgs[2]))
                    .reason(combineStringArgs[4])
                    .deadline(getTimeFromDuration(combineStringArgs[3]))
                    .ticketType(TicketType.ISSUE)
                    .targetPlayerDiscordId(targetPlayerDiscordId)
                    .build();

            discordSRVManager.sendMEssageToDiscord(ticket);
            ticketManager.addTicketToPlayer(targetPlayer, ticket);
            police.sendMessage("Штраф успешно выписан");

        } else {
            sender.sendMessage("Только игроки могут отправлять эту комманду");
            return true;
        }
        return true;
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
                                        .commandValue("Длительность")
                                        .required(true)
                                        .argumentType(ArgumentType.DURATION)
                                        .subCommands(List.of(SubCommand.builder()
                                                .commandValue("Причина")
                                                .argumentType(ArgumentType.SOME_VALUE)
                                                .required(true)
                                                .build()))
                                        .build()))
                                .build()))
                        .build()))
                .build();
    }
}
