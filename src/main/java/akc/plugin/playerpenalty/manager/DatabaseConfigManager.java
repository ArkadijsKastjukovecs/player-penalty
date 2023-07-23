package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.DatabaseConfigurationField;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DatabaseConfigManager {

    private final FileConfiguration configuration;
    private final File configFile;
    private final String workingDirectory;

    public DatabaseConfigManager(PlayerPenaltyPlugin plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        this.workingDirectory = plugin.getDataFolder().getAbsolutePath();
        this.configFile = new File(plugin.getDataFolder(), "config-database.yml");

        try {
            configFile.createNewFile();
            this.configuration = new YamlConfiguration();
            configuration.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void populateDefaultValues() {
        Arrays.stream(DatabaseConfigurationField.values())
                .filter(field -> configuration.getString(field.getName()) == null)
                .forEach(field -> {
                    configuration.set(field.getName(), field.getDefaultValue());
                    configuration.setComments(field.getName(), List.of(field.getComment()));
                });
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public String getConfigValue(DatabaseConfigurationField field) {
        return configuration.getString(field.getName());
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void save() {
        try {
            configuration.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }
}
