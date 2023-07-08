package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.ArgumentType;
import akc.plugin.playerpenalty.domain.Ticket;
import akc.plugin.playerpenalty.domain.TicketType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static java.util.function.Predicate.not;

public class ForgiveCommand extends AbstractCommand {

    public ForgiveCommand(PlayerPenaltyPlugin plugin) {
        super(plugin, "forgive", List.of(Player.class));
    }

    @Override
    protected boolean handleCommand(CommandSender sender, Ticket newTicket, String[] args) {
        newTicket.setTicketType(TicketType.FORGIVE);

        final var originalTicket = ticketManager.findOriginalTicket(newTicket.getTicketNumber());
        originalTicket.setResolved(true);
        newTicket.setResolved(true);

        plugin.getDiscordSRVManager().sendMEssageToDiscord(newTicket);
        ticketManager.addTicketToPlayer(newTicket.getTargetPlayer(), newTicket);
        ticketManager.save(originalTicket);
        plugin.getScheduledTaskHandler().cancelTask(originalTicket);
        sender.sendMessage("Штраф под номером %s прощен".formatted(originalTicket.getTicketNumber()));

        return true;
    }

    @Override
    protected List<SubCommand<?>> createSubCommands() {
        return List.of(createSubCommand());
    }

    private SubCommand<?> createSubCommand() {
        return SubCommand.<Ticket>builder()
                .commandValue("Номер_тикета")
                .argumentType(ArgumentType.TICKET_NUMBER)
                .buildAppender((newTicket, foundTicket) -> foundTicket.copyTo(newTicket))
                .validationFunction(validationManager.getTicketNumberValidationFunction())
                .playerValueTransformer(transformerManager.getVictimTicketNumberTransformer())
                .customSuggestionProvider(this::getOpenIssues)
                .required(true)
                .build();
    }

    private List<String> getOpenIssues(Player player) {
        return ticketManager.findOpenIssueByVictim(player).stream()
                .filter(not(Ticket::isResolved))
                .map(Ticket::getTicketNumber)
                .toList();
    }
}
