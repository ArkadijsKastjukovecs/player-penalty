package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.ArgumentType;
import akc.plugin.playerpenalty.domain.Ticket;
import akc.plugin.playerpenalty.domain.TicketType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ForgiveCommand extends AbstractCommand {

    public ForgiveCommand(PlayerPenaltyPlugin plugin) {
        super(List.of(createSubCommand()), plugin, "forgive");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {
            final var invalidArgument = validateArgs(args);
            if (invalidArgument != null) {
                sender.sendMessage("не удалось распознать параметр %s".formatted(invalidArgument));
                return true;
            }

            final var issueTicket = ticketManager.findOpenIssuebyVictim(player).stream()
                    .filter(ticket -> ticket.getTicketNumber().equals(args[0]))
                    .findAny()
                    .orElseThrow();

            final var forgiveTicket = createForgiveTicket(issueTicket);
            issueTicket.markAsResolved();
            plugin.getDiscordSRVManager().sendMEssageToDiscord(forgiveTicket);
            ticketManager.addTicketToPlayer(forgiveTicket.getTargetPlayer(), forgiveTicket);
        } else {
            sender.sendMessage("Только игроки могут отправлять эту комманду");
            return true;
        }

        return true;
    }

    private Ticket createForgiveTicket(Ticket originalTicket) {
        return originalTicket.copyBuilder()
                .ticketType(TicketType.FORGIVE)
                .build();
    }

    private String validateArgs(String[] args) {
        // TODO
        return null;
    }

    private static SubCommand createSubCommand() {
        return SubCommand.builder()
                .commandValue("Номер_тикета")
                .argumentType(ArgumentType.TICKET_NUMBER)
                .required(true)
                .build();
    }
}
