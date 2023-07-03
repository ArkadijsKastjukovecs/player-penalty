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
                .filter(ticket -> TicketType.isIssue(ticket.getTicketType()))
                .filter(not(Ticket::isResolved))
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

    public List<Ticket> findOpenIssueByVictim(Player player) {
        return playerTicketMap.values().stream()
                .flatMap(Collection::stream)
                .filter(not(Ticket::isResolved))
                .filter(it -> it.getVictim().equals(player))
                .toList();
    }

    public Ticket findOriginalTicket(String ticketNumber) {
        return playerTicketMap.values().stream()
                .flatMap(Collection::stream)
                .filter(it -> it.getTicketNumber().equals(ticketNumber))
                .findFirst().orElse(null);
    }

    public Ticket save(Ticket ticketToSave) {
        // TODO implement when DB is connected
        return ticketToSave;
    }
}
