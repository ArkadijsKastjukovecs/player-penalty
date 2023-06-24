package akc.plugin.playerpenalty.manager;

import akc.plugin.playerpenalty.PlayerPenaltyPlugin;
import akc.plugin.playerpenalty.config.ConfigurationFields;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MainConfigManager {

    private final FileConfiguration configuration;
    private final File configFile;

    public MainConfigManager(PlayerPenaltyPlugin plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        this.configFile = new File(plugin.getDataFolder(), "config.yml");

        try {
            configFile.createNewFile();
            this.configuration = new YamlConfiguration();
            configuration.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void populateDefaultValues() {
        Arrays.stream(ConfigurationFields.values())
                .filter(field -> configuration.getString(field.getName()) == null)
                .forEach(field -> configuration.set(field.getName(), field.getDefaultValue()));
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public String getConfigValue(ConfigurationFields field) {
        return configuration.getString(field.getName());
    }

    public void save() {
        try {
            configuration.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }
}
