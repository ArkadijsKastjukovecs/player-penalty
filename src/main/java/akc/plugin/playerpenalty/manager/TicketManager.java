package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.Ticket;
import akc.plugin.playerpenalty.domain.TicketType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class TicketManager {

    private final Map<Player, List<Ticket>> playerTicketMap = new HashMap<>();
    private final PlayerPenaltyPlugin plugin;

    public TicketManager(PlayerPenaltyPlugin plugin) {
        this.plugin = plugin;
    }

    public void initTicketManager() {

    }

    public List<Ticket> findTicketsOnPlayer(Player player) {
        return Optional.ofNullable(playerTicketMap.get(player))
                .orElse(Collections.emptyList());
    }

    public List<Ticket> findOpenIssues(Player player) {
        return findTicketsOnPlayer(player).stream()
                .filter(ticket -> ticket.getTicketType().equals(TicketType.ISSUE))
                .filter(not(Ticket::isPaid))
                .collect(Collectors.toList());
    }

    public void addTicketToPlayer(Player player, Ticket ticket) {
        System.out.println("adding ticket with number %s to player %s".formatted(ticket.getTicketNumber(), player.getName()));
        if (playerTicketMap.containsKey(player)) {
            playerTicketMap.get(player).add(ticket);
            return;
        }

        final var ticketList = new ArrayList<Ticket>();
        ticketList.add(ticket);
        playerTicketMap.put(player, ticketList);
    }

}