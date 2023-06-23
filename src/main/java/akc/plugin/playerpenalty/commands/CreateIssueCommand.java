package akc.plugin.playerpenalty.commands;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreateIssueCommand extends AbstractCommand {

    private final PlayerPenaltyPlugin plugin;

    public CreateIssueCommand(PlayerPenaltyPlugin plugin) {
        super(List.of(createSubCommand()), "createIssue");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        return true;
    }

    private static SubCommand createSubCommand() {
        return SubCommand.builder()
                .commandValue("Преступник")
                .argumentType(ArgumentType.PLAYER)
                .required(true)
                .subCommands(List.of(SubCommand.builder()
                        .commandValue("Жертва")
                        .argumentType(ArgumentType.PLAYER)
                        .required(true)
                        .subCommands(List.of(SubCommand.builder()
                                .commandValue("количество")
                                .argumentType(ArgumentType.SOME_VALUE)
                                .required(true)
                                .subCommands(List.of(SubCommand.builder()
                                        .commandValue("Причина")
                                        .argumentType(ArgumentType.SOME_VALUE)
                                        .required(true)
                                        .subCommands(List.of(SubCommand.builder()
                                                .commandValue("Длительность")
                                                .required(true)
                                                .argumentType(ArgumentType.DURATION)
                                                .build()))
                                        .build()))
                                .build()))
                        .build()))
                .build();
    }
}
