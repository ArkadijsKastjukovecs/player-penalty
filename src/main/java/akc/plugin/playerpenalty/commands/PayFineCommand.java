package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.ArgumentType;
import akc.plugin.playerpenalty.domain.TicketType;
import akc.plugin.playerpenalty.domain.entities.TicketEntity;
import akc.plugin.playerpenalty.manager.PlayerPointsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PayFineCommand extends AbstractCommand {

    private final PlayerPointsManager playerPointsManager;

    public PayFineCommand(PlayerPenaltyPlugin plugin) {
        super(plugin, "payFine", List.of(Player.class));
        this.playerPointsManager = plugin.getPlayerPointsManager();
    }

    @Override
    protected boolean handleCommand(CommandSender sender, TicketEntity newTicket, String[] args) {
        newTicket.setTicketType(TicketType.PARDON)
                .setShouldBePaid(false);
        final var originalTicket = newTicket.getSourceTicket();

        final var pointsAPI = playerPointsManager.getPointsAPI();
        final var ticketPaid = pointsAPI.pay(UUID.fromString(newTicket.getTargetPlayer().getPlayerId()), UUID.fromString(newTicket.getVictim().getPlayerId()), newTicket.getPenaltyAmount());

        if (ticketPaid) {

            cancelSchedule(originalTicket);
            originalTicket.setShouldBePaid(false);
            ticketRepository.saveNewTicket(newTicket);
            plugin.getDiscordSRVManager().sendMEssageToDiscord(newTicket);
            ticketRepository.updateExistingTicket(originalTicket);
            sender.sendMessage("Штраф под номером %s успешно оплачен".formatted(originalTicket.getId()));
        } else {
            sender.sendMessage("Произошла ошибка оплаты штрафа под номером %s".formatted(originalTicket.getId()));
        }

        return true;
    }

    @Override
    protected List<SubCommand<?>> createSubCommands() {
        return List.of(createSubCommand());
    }

    private void cancelSchedule(TicketEntity originalTicket) {
        Optional.ofNullable(originalTicket.getSchedule())
                .ifPresent(schedule -> {
                    schedule.setActive(false);
                    Bukkit.getScheduler().cancelTask(schedule.getBukkitTaskId());
                });
    }

    private SubCommand<?> createSubCommand() {
        return SubCommand.<TicketEntity>builder()
                .commandValue("Номер_тикета")
                .argumentType(ArgumentType.TICKET_NUMBER)
                .buildAppender((emptyTicket, foundTicket) -> foundTicket.copyTo(emptyTicket))
                .validationFunction(validationManager.getTicketNumberValidationFunction())
                .playerValueTransformer(transformerManager.getTargetPlayerTicketNumberTransformer())
                .customSuggestionProvider(this::getOpenTickets)
                .build();
    }

    @NotNull
    private List<String> getOpenTickets(Player player) {
        return ticketRepository.findOpenIssues(player).stream()
                .filter(TicketEntity::getShouldBePaid)
                .map(TicketEntity::getId)
                .map(String::valueOf)
                .toList();
    }
}
