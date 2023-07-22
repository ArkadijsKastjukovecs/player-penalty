package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.ConfigurationField;
import akc.plugin.playerpenalty.domain.entities.TicketEntity;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
            :receipt: Игрок %s просрочил свой штраф %s, размер текущего штрафа под номером %s составляет %s AP!
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
    private final String skinApi;

    public DiscordMessageSender(PlayerPenaltyPlugin plugin, TextChannel channelToSend) {
        String timeDisplayFormat = plugin.getConfigManager().getConfigValue(ConfigurationField.TIME_DISPLAY_FORMAT);
        String locale = plugin.getConfigManager().getConfigValue(ConfigurationField.LOCALE);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(timeDisplayFormat, new Locale(locale));
        this.channelToSend = channelToSend;
        this.skinApi = plugin.getConfigManager().getConfigValue(ConfigurationField.SKIN_API);
    }

    public void sendMessageToDiscord(TicketEntity ticket) {

        switch (ticket.getTicketType()) {
            case ISSUE -> sendIssueTicket(ticket);
            case DOUBLE_ISSUE -> sendDoubleIssueTicket(ticket);
            case PARDON -> sendPardonTicket(ticket);
            case FORGIVE -> sendForgiveTicket(ticket);
        }
    }

    private void sendPardonTicket(TicketEntity ticket) {
        channelToSend.sendMessage(USER_MENTION_TEMPLATE.formatted(ticket.getTargetPlayer().getPlayerDiscordId()))
                .setEmbeds(createPayFineEmbed(ticket)).queue();
    }

    private void sendIssueTicket(TicketEntity ticket) {
        channelToSend.sendMessage(USER_MENTION_TEMPLATE.formatted(ticket.getTargetPlayer().getPlayerDiscordId()))
                .setEmbeds(createIssueEmbed(ticket)).queue();
    }

    private void sendForgiveTicket(TicketEntity ticket) {
        channelToSend.sendMessage(USER_MENTION_TEMPLATE.formatted(ticket.getTargetPlayer().getPlayerDiscordId()))
                .setEmbeds(createForgiveEmbed(ticket)).queue();
    }

    private void sendDoubleIssueTicket(TicketEntity ticket) {
        channelToSend.sendMessage(USER_MENTION_TEMPLATE.formatted(ticket.getTargetPlayer().getPlayerDiscordId()))
                .setEmbeds(createDoubleIssueEmbed(ticket)).queue();
    }

    private MessageEmbed createIssueEmbed(TicketEntity ticket) {
        final var formattedDate = ticket.getSchedule().getDeadline().atOffset(ZoneOffset.UTC).format(dateTimeFormatter);
        return new EmbedBuilder()
                .setColor(ticket.getTicketType().getTicketColor())
                .setThumbnail(skinApi.formatted(ticket.getTargetPlayer().getPlayerId()))
                .addField(
                        ISSUE_FIELD_NAME_TEMPLATE.formatted(ticket.getTargetPlayer().getPlayerName(), ticket.getId(), ticket.getPenaltyAmount()),
                        ISSUE_FIELD_VALUE_TEMPLATE.formatted(ticket.getPolicePlayer().getPlayerName(), ticket.getVictim().getPlayerName(), ticket.getReason(), formattedDate),
                        false)
                .build();
    }

    private MessageEmbed createPayFineEmbed(TicketEntity ticket) {
        final var originalTicketNumber = ticket.getSourceTicket().getId();
        return new EmbedBuilder()
                .setColor(ticket.getTicketType().getTicketColor())
                .setThumbnail(skinApi.formatted(ticket.getTargetPlayer().getPlayerId()))
                .addField(
                        PAY_FINE_FIELD_NAME_TEMPLATE.formatted(originalTicketNumber, ticket.getTargetPlayer().getPlayerName(), ticket.getVictim().getPlayerName()),
                        PAY_FINE_FIELD_VALUE_TEMPLATE,
                        false)
                .build();
    }

    private MessageEmbed createForgiveEmbed(TicketEntity ticket) {
        final var originalTicketNumber = ticket.getSourceTicket().getId();
        return new EmbedBuilder()
                .setColor(ticket.getTicketType().getTicketColor())
                .setThumbnail(skinApi.formatted(ticket.getTargetPlayer().getPlayerId()))
                .addField(
                        FORGIVE_FIELD_NAME_TEMPLATE.formatted(originalTicketNumber, ticket.getTargetPlayer().getPlayerName(), ticket.getVictim().getPlayerName()),
                        PAY_FINE_FIELD_VALUE_TEMPLATE,
                        false)
                .build();
    }

    private MessageEmbed createDoubleIssueEmbed(TicketEntity ticket) {
        final var originalTicket = ticket.getSourceTicket();
        final var originalTicketNumber = originalTicket.getId();
        final var formattedDate = originalTicket.getSchedule().getDeadline().atOffset(ZoneOffset.UTC).format(dateTimeFormatter);
        return new EmbedBuilder()
                .setColor(ticket.getTicketType().getTicketColor())
                .setThumbnail(skinApi.formatted(ticket.getTargetPlayer().getPlayerId()))
                .addField(
                        DOUBLE_ISSUE_FIELD_NAME_TEMPLATE.formatted(ticket.getTargetPlayer().getPlayerName(), originalTicketNumber, ticket.getId(), ticket.getPenaltyAmount()),
                        DOUBLE_ISSUE_FIELD_VALUE_TEMPLATE.formatted(ticket.getPolicePlayer().getPlayerName(), ticket.getVictim().getPlayerName(), ticket.getReason(), formattedDate),
                        false)
                .build();
    }
}
