package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.commands.transformation.*;

public class TransformerManager {

    private final PlayerTransformer playerTransformer;
    private final NumberTransformer numberTransformer;
    private final DurationTransformer durationTransformer;
    private final SomeValueTransformer someValueTransformer;
    private final TargetPlayerTicketNumberTransformer targetPlayerTicketNumberTransformer;
    private final VictimTicketNumberTransformer victimTicketNumberTransformer;

    public TransformerManager(PlayerPenaltyPlugin plugin) {
        this.playerTransformer = new PlayerTransformer();
        this.numberTransformer = new NumberTransformer();
        this.durationTransformer = new DurationTransformer(plugin);
        this.someValueTransformer = new SomeValueTransformer();
        this.targetPlayerTicketNumberTransformer = new TargetPlayerTicketNumberTransformer(plugin);
        this.victimTicketNumberTransformer = new VictimTicketNumberTransformer(plugin);
    }

    public PlayerTransformer getPlayerTransformer() {
        return playerTransformer;
    }

    public NumberTransformer getNumberTransformer() {
        return numberTransformer;
    }

    public DurationTransformer getDurationTransformer() {
        return durationTransformer;
    }

    public SomeValueTransformer getSomeValueTransformer() {
        return someValueTransformer;
    }

    public TargetPlayerTicketNumberTransformer getTargetPlayerTicketNumberTransformer() {
        return targetPlayerTicketNumberTransformer;
    }

    public VictimTicketNumberTransformer getVictimTicketNumberTransformer() {
        return victimTicketNumberTransformer;
    }
}
