package fr.chilli.util;


import fr.chilli.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class DataSaver {

    private static final File DATA_FOLDER = new File(Main.getPlugin(Main.class).getDataFolder(), "playerdata");

    public static void addPerms(String nouveau,String bankname){
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getFile(bankname));

        configuration.set(nouveau,true);

        try {
            configuration.save(getFile(bankname));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delPerms(String nouveau,String bankname){
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getFile(bankname));

        configuration.set(nouveau,null);

        try {
            configuration.save(getFile(bankname));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkPerms(String nouveau, String bankname) {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getFile(bankname));
        String auth = configuration.getString("owner");

        if ((configuration.contains(nouveau))) return true;
        assert auth != null;
        return auth.equalsIgnoreCase(nouveau);
    }
}
