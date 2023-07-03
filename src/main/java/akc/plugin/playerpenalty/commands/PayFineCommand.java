package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.ArgumentType;
import akc.plugin.playerpenalty.domain.Ticket;
import akc.plugin.playerpenalty.domain.TicketType;
import akc.plugin.playerpenalty.manager.PlayerPointsManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.util.function.Predicate.not;

public class PayFineCommand extends AbstractCommand {

    private final PlayerPointsManager playerPointsManager;

    public PayFineCommand(PlayerPenaltyPlugin plugin) {
        super(new ArrayList<>(), plugin, "payFine", List.of(Player.class));
        subCommands.add(createSubCommand());
        this.playerPointsManager = plugin.getPlayerPointsManager();
    }

    @Override
    protected boolean handleCommand(CommandSender sender, Ticket newTicket, String[] args) {
        newTicket.setTicketType(TicketType.PARDON);

        final var originalTicket = ticketManager.findOriginalTicket(newTicket.getTicketNumber());

        final var pointsAPI = playerPointsManager.getPointsAPI();
        final var ticketPaid = pointsAPI.pay(newTicket.getTargetPlayer().getUniqueId(), newTicket.getVictim().getUniqueId(), newTicket.getPenaltyAmount());

        if (ticketPaid) {
            originalTicket.setResolved(true);
            newTicket.setResolved(true);
            plugin.getDiscordSRVManager().sendMEssageToDiscord(newTicket);
            ticketManager.addTicketToPlayer(newTicket.getTargetPlayer(), newTicket);
            ticketManager.save(originalTicket);
            plugin.getScheduledTaskHandler().cancelTask(originalTicket);
            sender.sendMessage("Штраф под номером %s успешно оплачен".formatted(newTicket.getTicketNumber()));
        } else {
            sender.sendMessage("Произошла ошибка оплаты штрафа под номером %s".formatted(newTicket.getTicketNumber()));
        }

        return true;
    }

    private SubCommand<?> createSubCommand() {
        return SubCommand.<Ticket>builder()
                .commandValue("Номер_тикета")
                .argumentType(ArgumentType.TICKET_NUMBER)
                .buildAppender((emptyTicket, foundTicket) -> foundTicket.copyTo(emptyTicket))
                .validationFunction(validationManager.getTicketNumberValidationFunction())
                .playerValueTransformer(transformerManager.getTargetPlayerTicketNumberTransformer())
                .customSuggestionProvider(this::getOpenTickets)
                .required(true)
                .build();
    }

    @NotNull
    private List<String> getOpenTickets(Player player) {
        return ticketManager.findOpenIssues(player).stream()
                .filter(not(Ticket::isResolved))
                .map(Ticket::getTicketNumber)
                .toList();
    }
}
