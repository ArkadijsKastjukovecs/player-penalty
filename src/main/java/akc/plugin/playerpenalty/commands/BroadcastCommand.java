package akc.plugin.playerpenalty.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BroadcastCommand extends AbstractCommand {
    public BroadcastCommand() {
        super(List.of(createSubcommand()), "broadcast");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        sender.sendMessage(args);
        return true;
    }

    private static SubCommand createSubcommand() {
        return SubCommand.builder()
                .argumentType(ArgumentType.PLAYER)
                .commandValue("Player")
                .subCommands(List.of(SubCommand.builder()
                        .argumentType(ArgumentType.COMMAND)
                        .commandValue("someCommand")
                        .subCommands(List.of(SubCommand.builder()
                                .argumentType(ArgumentType.COMMAND)
                                .commandValue("inner value")
                                .build()))
                        .build()))
                .build();
    }
}
