package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.ConfigurationFields;
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
                        
            Немедленно выплатите его пострадавшему, иначе со временем он удвоится!
            """;

    private static final String PAY_FINE_FIELD_NAME_TEMPLATE = """
            :people_hugging: Штраф %s игрока %s был оплачен игроку %s!
            """;
    private static final String PAY_FINE_FIELD_VALUE_TEMPLATE = """
                        
            В следующий раз больше не нарушайте!
            """;

    private static final String FORGIVE_FIELD_NAME_TEMPLATE = """
            :people_hugging: Штраф %s игрока %s был прощен игроком %s!
            """;

    private static final String DOUBLE_ISSUE_FIELD_NAME_TEMPLATE = """
            :receipt: Игрок %s просрочил свой штраф %s, резмер текущего штрафа под номером %s составляет %s AP!
            """;
    private static final String DOUBLE_ISSUE_FIELD_VALUE_TEMPLATE = """
                        
            :police_officer:  **Выписал — %s**
            :bust_in_silhouette: **Пострадавший — %s**
            :notepad_spiral: **Причина — %s**
            :alarm_clock: **Срок выплаты — ~~%s~~ просрочено**
                        
            Немедленно выплатите его пострадавшему!
            """;
    private static final String USER_MENTION_TEMPLATE = "<@%s>";

    private final DateTimeFormatter dateTimeFormatter;
    private final TextChannel channelToSend;

    public DiscordMessageSender(PlayerPenaltyPlugin plugin, TextChannel channelToSend) {
        String timeDisplayFormat = plugin.getConfigManager().getConfigValue(ConfigurationFields.TIME_DISPLAY_FORMAT);
        String locale = plugin.getConfigManager().getConfigValue(ConfigurationFields.LOCALE);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(timeDisplayFormat, new Locale(locale));
        this.channelToSend = channelToSend;
    }

    public void sendMessageToDiscord(Ticket ticket) {

        switch (ticket.getTicketType()) {
            case ISSUE -> sendIssueTicket(ticket);
            case DOUBLE_ISSUE -> sendDoubleIssueTicket(ticket);
            case PARDON -> sendPardonTicket(ticket);
            case FORGIVE -> sendForgiveTicket(ticket);
        }
    }

    private void sendPardonTicket(Ticket ticket) {
        channelToSend.sendMessage(USER_MENTION_TEMPLATE.formatted(ticket.getTargetPlayerDiscordId())).queue(message ->
                message.editMessageEmbeds(List.of(createPayFineEmbed(ticket, message))).queue());
    }

    private void sendIssueTicket(Ticket ticket) {
        channelToSend.sendMessage(USER_MENTION_TEMPLATE.formatted(ticket.getTargetPlayerDiscordId())).queue(message ->
                message.editMessageEmbeds(List.of(createIssueEmbed(ticket, message))).queue());
    }

    private void sendForgiveTicket(Ticket ticket) {
        channelToSend.sendMessage(USER_MENTION_TEMPLATE.formatted(ticket.getTargetPlayerDiscordId())).queue(message ->
                message.editMessageEmbeds(List.of(createForgiveEmbed(ticket, message))).queue());
    }

    private void sendDoubleIssueTicket(Ticket ticket) {
        channelToSend.sendMessage(USER_MENTION_TEMPLATE.formatted(ticket.getTargetPlayerDiscordId())).queue(message ->
                message.editMessageEmbeds(List.of(createDoubleIssueEmbed(ticket, message))).queue());
    }

    private MessageEmbed createIssueEmbed(Ticket ticket, Message message) {
        final var formattedDate = ticket.getDeadline().atOffset(ZoneOffset.UTC).format(dateTimeFormatter);
        ticket.setTicketNumber(message.getId());
        return new EmbedBuilder()
                .setColor(ticket.getTicketType().getTicketColor())
//                .setThumbnail(null) // TODO
                .addField(
                        ISSUE_FIELD_NAME_TEMPLATE.formatted(ticket.getTargetPlayer().getName(), message.getId(), ticket.getPenaltyAmount()),
                        ISSUE_FIELD_VALUE_TEMPLATE.formatted(ticket.getPolicePlayer().getName(), ticket.getVictim().getName(), ticket.getReason(), formattedDate),
                        false)
                .build();
    }

    private MessageEmbed createPayFineEmbed(Ticket ticket, Message message) {
        final var originalTicketNumber = ticket.getTicketNumber();
        ticket.setTicketNumber(message.getId());
        return new EmbedBuilder()
                .setColor(ticket.getTicketType().getTicketColor())
//                .setThumbnail(null) // TODO
                .addField(
                        PAY_FINE_FIELD_NAME_TEMPLATE.formatted(originalTicketNumber, ticket.getTargetPlayer().getName(), ticket.getVictim().getName()),
                        PAY_FINE_FIELD_VALUE_TEMPLATE,
                        false)
                .build();
    }

    private MessageEmbed createForgiveEmbed(Ticket ticket, Message message) {
        final var originalTicketNumber = ticket.getTicketNumber();
        ticket.setTicketNumber(message.getId());
        return new EmbedBuilder()
                .setColor(ticket.getTicketType().getTicketColor())
//                .setThumbnail(null) // TODO
                .addField(
                        FORGIVE_FIELD_NAME_TEMPLATE.formatted(originalTicketNumber, ticket.getTargetPlayer().getName(), ticket.getVictim().getName()),
                        PAY_FINE_FIELD_VALUE_TEMPLATE,
                        false)
                .build();
    }

    private MessageEmbed createDoubleIssueEmbed(Ticket ticket, Message message) {
        final var originalTicketNumber = ticket.getTicketNumber();
        final var formattedDate = ticket.getDeadline().atOffset(ZoneOffset.UTC).format(dateTimeFormatter);
        ticket.setTicketNumber(message.getId());
        return new EmbedBuilder()
                .setColor(ticket.getTicketType().getTicketColor())
//                .setThumbnail(null) // TODO
                .addField(
                        DOUBLE_ISSUE_FIELD_NAME_TEMPLATE.formatted(ticket.getTargetPlayer().getName(), originalTicketNumber, message.getId(), ticket.getPenaltyAmount()),
                        DOUBLE_ISSUE_FIELD_VALUE_TEMPLATE.formatted(ticket.getPolicePlayer().getName(), ticket.getVictim().getName(), ticket.getReason(), formattedDate),
                        false)
                .build();
    }
}
