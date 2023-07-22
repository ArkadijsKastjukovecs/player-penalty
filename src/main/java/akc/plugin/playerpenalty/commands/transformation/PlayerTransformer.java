package akc.plugin.playerpenalty.commands.transformation;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.domain.entities.PlayerEntity;
import akc.plugin.playerpenalty.repository.TicketRepository;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.function.Function;

public class PlayerTransformer implements Function<String, PlayerEntity> {

    private final TicketRepository ticketRepository;

    public PlayerTransformer(PlayerPenaltyPlugin plugin) {
        this.ticketRepository = plugin.getTicketRepository();
    }


    @Override
    public PlayerEntity apply(String playerName) {
        final var player = Optional.ofNullable(Bukkit.getPlayer(playerName))
                .orElseGet(() -> Bukkit.getOfflinePlayer(playerName).getPlayer());
        return ticketRepository.getOrCreatePlayer(player);
    }
}
