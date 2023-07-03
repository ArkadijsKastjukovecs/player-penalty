package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.ArgumentType;
import akc.plugin.playerpenalty.domain.Ticket;
import akc.plugin.playerpenalty.domain.TicketType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ForgiveCommand extends AbstractCommand {

    public ForgiveCommand(PlayerPenaltyPlugin plugin) {
        super(new ArrayList<>(), plugin, "forgive", List.of(Player.class));
        subCommands.add(createSubCommand());
    }

    @Override
    protected boolean handleCommand(CommandSender sender, Ticket newTicket, String[] args) {
        newTicket.setTicketType(TicketType.FORGIVE);

        final var originalTicket = ticketManager.findOriginalTicket(newTicket.getTicketNumber());
        originalTicket.setResolved(true);

        plugin.getDiscordSRVManager().sendMEssageToDiscord(newTicket);
        ticketManager.addTicketToPlayer(newTicket.getTargetPlayer(), newTicket);
        ticketManager.save(originalTicket);

        return true;
    }

    private SubCommand<?> createSubCommand() {
        return SubCommand.<Ticket>builder()
                .commandValue("Номер_тикета")
                .argumentType(ArgumentType.TICKET_NUMBER)
                .buildAppender((newTicket, foundTicket) -> foundTicket.copyTo(newTicket))
                .validationFunction(validationManager.getTicketNumberValidationFunction())
                .playerValueTransformer(transformerManager.getVictimTicketNumberTransformer())
                .customSuggestionProvider(player -> ticketManager.findOpenIssuebyVictim(player).stream().map(Ticket::getTicketNumber).toList())
                .required(true)
                .build();
    }
}
