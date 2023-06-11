package fr.chilli.util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class UtilsPlayer {
    private FileConfiguration playerConfig;
    private File playerFile;

    public UtilsPlayer() {
        // Créer ou charger le fichier de configuration des joueurs
        playerFile = new File("playerData.yml");
        playerConfig = YamlConfiguration.loadConfiguration(playerFile);
    }

    public void savePlayer(UUID playerUUID) {
        // Enregistrer le joueur dans le fichier de configuration
        playerConfig.set("players." + playerUUID.toString(), true);

        // Sauvegarder le fichier de configuration
        try {
            playerConfig.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(UUID playerUUID) {
        // Vérifier si le joueur existe dans le fichier de configuration
        return playerConfig.getBoolean("players." + playerUUID.toString(), false);
    }
}

