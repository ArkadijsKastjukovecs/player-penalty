package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.ArgumentType;
import akc.plugin.playerpenalty.domain.Ticket;
import akc.plugin.playerpenalty.domain.TicketType;
import akc.plugin.playerpenalty.manager.DiscordSRVManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreateIssueCommand extends AbstractCommand {

    private final int minRequiredLenght;
    private final DiscordSRVManager discordSRVManager;

    public CreateIssueCommand(PlayerPenaltyPlugin plugin) {
        super(new ArrayList<>(), plugin, "createIssue", List.of(Player.class));
        this.subCommands.add(createSubCommand());
        this.minRequiredLenght = 5;
        this.discordSRVManager = plugin.getDiscordSRVManager();
    }

    @Override
    protected final boolean handleCommand(CommandSender sender, Ticket ticket, String[] args) {
        var player = (Player) sender;

        final var targetPlayer = ticket.getTargetPlayer();
        final var discordId = discordSRVManager.getDiscordId(targetPlayer);
        ticket.setPolicePlayer(player)
                .setTicketType(TicketType.ISSUE)
                .setTargetPlayerDiscordId(discordId);

        discordSRVManager.sendMEssageToDiscord(ticket);
        ticketManager.addTicketToPlayer(targetPlayer, ticket);

        return false;
    }

    private SubCommand createSubCommand() {
        return SubCommand.<Player>builder()
                .commandValue("Преступник")
                .argumentType(ArgumentType.PLAYER)
                .buildAppender(Ticket::setTargetPlayer)
                .valueTransformer(transformerManager.getPlayerTransformer())
                .validationFunction(validationManager.getPlayerValidator())
                .required(true)
                .subCommands(List.of(SubCommand.<Player>builder()
                        .commandValue("Жертва")
                        .argumentType(ArgumentType.PLAYER)
                        .buildAppender(Ticket::setVictim)
                        .valueTransformer(transformerManager.getPlayerTransformer())
                        .validationFunction(validationManager.getPlayerValidator())
                        .required(true)
                        .subCommands(List.of(SubCommand.<Integer>builder()
                                .commandValue("количество")
                                .argumentType(ArgumentType.NUMBER)
                                .buildAppender(Ticket::setPenaltyAmount)
                                .validationFunction(validationManager.getNumberValidationFunction())
                                .valueTransformer(transformerManager.getNumberTransformer())
                                .required(true)
                                .subCommands(List.of(SubCommand.<LocalDateTime>builder()
                                        .commandValue("Длительность")
                                        .argumentType(ArgumentType.DURATION)
                                        .buildAppender(Ticket::setDeadline)
                                        .validationFunction(validationManager.getDurationValidationFunction())
                                        .valueTransformer(transformerManager.getDurationTransformer())
                                        .required(true)
                                        .subCommands(List.of(SubCommand.<String>builder()
                                                .commandValue("Причина")
                                                .argumentType(ArgumentType.SOME_VALUE)
                                                .buildAppender(Ticket::setReason)
                                                .valueTransformer(transformerManager.getSomeValueTransformer())
                                                .validationFunction(validationManager.getSomeValueValidationFunction())
                                                .required(true)
                                                .build()))
                                        .build()))
                                .build()))
                        .build()))
                .build();
    }
}
