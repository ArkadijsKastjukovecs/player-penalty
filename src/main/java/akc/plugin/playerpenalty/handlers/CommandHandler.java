package akc.plugin.playerpenalty.handlers;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;

public class CommandHandler {

    private final PlayerPenaltyPlugin plugin;

    public CommandHandler(PlayerPenaltyPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        plugin.getSupportedCommands().forEach(command -> {
            plugin.getCommand(command.getCommandName()).setExecutor(command);
        });
    }
}
