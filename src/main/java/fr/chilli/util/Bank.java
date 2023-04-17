package fr.chilli.util;

import fr.chilli.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class Bank {

    public static final String CURRENCY_NAME = "Pp";
    private static final File DATA_FOLDER = new File(Main.getPlugin(Main.class).getDataFolder(), "playerdata");

    private static File getFile(String bankname) {
        return new File(DATA_FOLDER, bankname + "-data.yml");
    }

    public void create(String bankName, UUID creatorUUID) {
        File bankFile = new File(DATA_FOLDER, bankName + ".yml");

        if (bankFile.exists()) {
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        config.set("owner", creatorUUID);
        config.set("balance", 0);
        config.set("created_date", new Date());

        try {
            config.save(bankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(String bankName) {
        File file = new File(DATA_FOLDER, bankName + ".yml");
        file.delete();
        if (file.delete()) {
            System.out.println("Banque " + bankName + " supprimée avec succès !");
        } else {
            System.out.println("Impossible de supprimer la banque " + bankName + ".");
        }
    }
    public boolean getOwnedBankByName(String bankName) {
        File bankDir = DATA_FOLDER;

            for (File bankFile : bankDir.listFiles()) {
                if (bankFile.getName().equals(bankName)) {
                    return true;
                }
            }
        return false;
    }

    public String getOwner(String bankName) {
        File bankFile = new File(DATA_FOLDER, bankName + ".yml");
        if (!bankFile.exists()) {
            return null;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
        return config.getString("owner");
    }
    public void quit (Player player){

    }

    public void setOwner(String newOwnerName, Player oldOwnerName) {

        // On modifie également le nom du propriétaire dans le fichier
        File bankFile = getFile(String.valueOf(oldOwnerName));
        YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);
        bankConfig.set("ownerName", newOwnerName);
        try {
            bankConfig.save(bankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void withdraw(double amount, String bankname) {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getFile(bankname));

        int money = configuration.getInt("money");
        double new_money = money - amount;

        configuration.set("money", new_money);

        try {
            configuration.save(getFile(bankname));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deposit(double amount, String bankName) {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getFile(bankName));

        int money = configuration.getInt("money");
        double new_money = money + amount;

        configuration.set("money",new_money);

        try {
            configuration.save(getFile(bankName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double balance(String bankName) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(getFile(bankName));

        return (double) config.get("money");
    }
}
