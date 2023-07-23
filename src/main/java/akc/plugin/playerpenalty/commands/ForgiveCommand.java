package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.ArgumentType;
import akc.plugin.playerpenalty.domain.TicketType;
import akc.plugin.playerpenalty.domain.entities.TicketEntity;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class ForgiveCommand extends AbstractCommand {

    public ForgiveCommand(PlayerPenaltyPlugin plugin) {
        super(plugin, "forgive", List.of(Player.class));
    }

    @Override
    protected boolean handleCommand(CommandSender sender, TicketEntity newTicket, String[] args) {
        newTicket.setTicketType(TicketType.FORGIVE)
                .setShouldBePaid(false);

        final var originalTicket = newTicket.getSourceTicket();
        cancelSchedule(originalTicket);
        originalTicket.setShouldBePaid(false);

        ticketRepository.saveNewTicket(newTicket);
        plugin.getDiscordSRVManager().sendMEssageToDiscord(newTicket);
        ticketRepository.updateExistingTicket(originalTicket);
        sender.sendMessage("Штраф под номером %s прощен".formatted(originalTicket.getId()));

        return true;
    }

    private void cancelSchedule(TicketEntity originalTicket) {
        Optional.ofNullable(originalTicket.getSchedule())
                .ifPresent(schedule -> {
                    schedule.setActive(false);
                    Bukkit.getScheduler().cancelTask(schedule.getBukkitTaskId());
                });
    }

    @Override
    protected List<SubCommand<?>> createSubCommands() {
        return List.of(createSubCommand());
    }

    private SubCommand<?> createSubCommand() {
        return SubCommand.<TicketEntity>builder()
                .commandValue("Номер_тикета")
                .argumentType(ArgumentType.TICKET_NUMBER)
                .buildAppender((newTicket, foundTicket) -> foundTicket.copyTo(newTicket))
                .validationFunction(validationManager.getTicketNumberValidationFunction())
                .playerValueTransformer(transformerManager.getVictimTicketNumberTransformer())
                .customSuggestionProvider(this::getOpenIssues)
                .build();
    }

    private List<String> getOpenIssues(Player player) {
        return ticketRepository.findOpenVictimTickets(player).stream()
                .filter(TicketEntity::getShouldBePaid)
                .map(TicketEntity::getId)
                .map(String::valueOf)
                .toList();
    }
}
