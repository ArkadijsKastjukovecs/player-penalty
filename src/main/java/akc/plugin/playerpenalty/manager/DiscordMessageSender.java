package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.domain.Ticket;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DiscordMessageSender {

    private static final String ISSUE_FIELD_NAME_TEMPLATE = """
            :receipt: Игроку %s был выписан штраф %s в размере %s AP!
            """;
    private static final String ISSUE_FIELD_VALUE_TEMPLATE = """
                        
            :police_officer:  **Выписал — %s**
            :bust_in_silhouette: **Пострадавший — %s**
            :notepad_spiral: **Причина — %s**
            :alarm_clock: **Срок выплаты — %s**
                        
            Немедленны выплатите его в банке или пострадавшему, иначе со временем он удвоится!
            """;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", new Locale("ru"));

    private final TextChannel channelToSend;

    public DiscordMessageSender(TextChannel channelToSend) {
        this.channelToSend = channelToSend;
    }

    public void sendMessageToDiscord(Ticket ticket) {

        switch (ticket.getTicketType()) {
            case ISSUE -> sendIssueTicket(ticket);
            default -> {
            }
        }
    }

    private void sendIssueTicket(Ticket ticket) {
        channelToSend.sendMessage("<@%s>".formatted(ticket.getTargetPlayerDiscordId())).queue(message ->
                message.editMessageEmbeds(List.of(createIssueEmbed(ticket, message))).queue());
    }

    private MessageEmbed createIssueEmbed(Ticket ticket, Message message) {
        final var formattedDate = ticket.getDeadline().atOffset(ZoneOffset.UTC).format(dateTimeFormatter);
        return new EmbedBuilder()
                .setColor(ticket.getTicketType().getTicketColor())
//                .setThumbnail(null) // TODO
                .addField(
                        ISSUE_FIELD_NAME_TEMPLATE.formatted(ticket.getTargetPlayer().getName(), message.getId(), ticket.getPenaltyAmount()),
                        ISSUE_FIELD_VALUE_TEMPLATE.formatted(ticket.getPolicePlayer().getName(), ticket.getVictim().getName(), ticket.getReason(), formattedDate),
                        false)
                .build();
    }
}
