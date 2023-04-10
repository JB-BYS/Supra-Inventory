package fr.chilli.util;


import fr.chilli.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class DataSaver {

    private static final File DATA_FOLDER = new File(Main.getPlugin(Main.class).getDataFolder(), "playerdata");


    public static void addMoney(Double money,String bankname){
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getFile(bankname));

        Integer pre_solde = configuration.getInt("money");
        double new_solde = pre_solde+money;

        configuration.set("money",new_solde);

        try {
            configuration.save(getFile(bankname));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void retirerMoney(Double money, String bankname) {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getFile(bankname));

        Integer pre_solde = configuration.getInt("money");
        double new_solde = pre_solde-money;

        configuration.set("money",new_solde);

        try {
            configuration.save(getFile(bankname));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static int loadMoney(String bankname) {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getFile(bankname));

        return configuration.getInt("money");
    }

    public static void deleteBank (String bankname) {
        delFile(bankname);
    }

    public static void createBank (Player p,String bankname){
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getFile(bankname));


        configuration.set("money",0);
        configuration.set("owner",p.getName());

        try {
            configuration.save(getFile(bankname));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkFile (String bankname) {
        File file = getFile(bankname);
        return file.exists();
    }


    private static File getFile(String bankname) {
        return new File(DATA_FOLDER, bankname + "-data.yml");
    }

    private static boolean delFile(String bankname) {
        File file = getFile(bankname);
        return file.delete();
    }

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

    public static boolean checkOwner(String owner, String bankname) {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getFile(bankname));
        String auth = configuration.getString("owner");

        assert auth != null;
        return auth.equalsIgnoreCase(owner);
    }
}
