package fr.chilli.util;

import fr.chilli.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

    public void trust(String bankName,UUID playerUUID) {
        if (playerUUID == null) {
            return;
        }

        File bankFile = new File(DATA_FOLDER, bankName + ".yml");
        YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);
        List<String> memberList = bankConfig.getStringList("members");
        memberList.add(playerUUID.toString());
        bankConfig.set("members", memberList);
        try {
            bankConfig.save(bankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void untrust(String bankName,UUID playerUUID) {
        if (playerUUID == null) {
            return;
        }

        File bankFile = new File(DATA_FOLDER, bankName + ".yml");
        YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);
        List<String> memberList = bankConfig.getStringList("members");
        memberList.remove(playerUUID.toString());
        bankConfig.set("members", memberList);
        try {
            bankConfig.save(bankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isTrusted(UUID playerUUID, String bankName) {
        File bankFile = new File(DATA_FOLDER, bankName + ".yml");
        if (bankFile != null) {
            YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);
            List<String> memberList = bankConfig.getStringList("members");
            return memberList.contains(playerUUID.toString());
        }
        return false;
    }

    public boolean bankNameAvailable(String bankName) {
        File bankDir = DATA_FOLDER;

        for (File bankFile : bankDir.listFiles()) {
            if (bankFile.getName().equals(bankName)) {
                return false;
            }
        }
        return true;
    }

    public void log(String playerName,String bankName, String action) {
        File file = new File(DATA_FOLDER + File.separator + "logs.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        String dateString = formatter.format(date);

        List<String> logs = config.getStringList("logs");
        logs.add(dateString + " - " + playerName + " - " + bankName + " - " + action);

        config.set("logs", logs);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
