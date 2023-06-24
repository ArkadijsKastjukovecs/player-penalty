package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.Ticket;
import akc.plugin.playerpenalty.domain.TicketType;
import akc.plugin.playerpenalty.manager.DiscordSRVManager;
import akc.plugin.playerpenalty.manager.PlayerPointsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PayFineCommand extends AbstractCommand {

    private final DiscordSRVManager discordSRVManager;
    private final PlayerPointsManager playerPointsManager;

    public PayFineCommand(PlayerPenaltyPlugin plugin) {
        super(List.of(createSubCommand()), plugin, "payFine");
        this.discordSRVManager = plugin.getDiscordSRVManager();
        this.playerPointsManager = plugin.getPlayerPointsManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {
            final var invalidArgument = validateArgs(args);
            if (invalidArgument != null) {
                sender.sendMessage("не удалось распознать параметр %s".formatted(invalidArgument));
                return true;
            }

            final var ticketToBePaid = ticketManager.findOpenIssues(player).stream()
                    .filter(ticket -> ticket.getTicketNumber().equals(args[0]))
                    .findAny()
                    .orElseThrow();

            final var pointsAPI = playerPointsManager.getPointsAPI();
            final var ticketPaid = pointsAPI.pay(player.getUniqueId(), ticketToBePaid.getVictim().getUniqueId(), ticketToBePaid.getPenaltyAmount());

            if (ticketPaid) {
                ticketToBePaid.markAsPaid();
                final var payFineTicket = createPayFineTicket(ticketToBePaid);
                discordSRVManager.sendMEssageToDiscord(payFineTicket);
                ticketManager.addTicketToPlayer(player, payFineTicket);
                player.sendMessage("Штраф под номером %s успешно оплачен".formatted(ticketToBePaid.getTicketNumber()));
            } else {
                player.sendMessage("Произошла ошибка оплаты штрафа под номером %s".formatted(ticketToBePaid.getTicketNumber()));
            }

        } else {
            sender.sendMessage("Только игроки могут отправлять эту комманду");
            return true;
        }
        return false;
    }

    private Ticket createPayFineTicket(Ticket originalTicket) {
        return originalTicket.copyBuilder()
                .ticketType(TicketType.PARDON)
                .build();
    }

    private String validateArgs(String[] args) {
        // TODO
        return null;
    }

    private static SubCommand createSubCommand() {
        return SubCommand.builder()
                .commandValue("Номер тикета")
                .argumentType(ArgumentType.TICKET_NUMBER)
                .required(true)
                .build();
    }
}
