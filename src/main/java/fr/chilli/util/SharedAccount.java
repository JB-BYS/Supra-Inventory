package fr.chilli.util;

import fr.chilli.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class SharedAccount {

    public static final String CURRENCY_NAME = ChatColor.YELLOW + "Pp";
    public static final String HELPER_WORDS = ChatColor.RED + " Si le problème subsiste veuillez contacter un admin";
    private static final File BANK_FOLDER = new File(Main.getPlugin(Main.class).getDataFolder(), "banks");
    private static final File ADMIN_FOLDER = new File(Main.getPlugin(Main.class).getDataFolder(), "admins");
    private static final File PLAYER_FOLDER = new File(Main.getPlugin(Main.class).getDataFolder(), "players");


    private static File getBankFile(String bankName) {
        return new File(BANK_FOLDER, bankName+ ".yml");
    }

    private static File getPlayerFile(UUID uuid) {
        return new File(PLAYER_FOLDER, uuid + ".yml");
    }




    public static boolean hasAccount(UUID playerUUID) {
        File accountsFile = getPlayerFile(playerUUID);
        if (!accountsFile.exists() || !accountsFile.isDirectory()) {
            return false;
        }

        File[] accountFiles = accountsFile.listFiles();
        if (accountFiles != null) {
            for (File file : accountFiles) {
                if (file.isFile() && file.getName().equalsIgnoreCase(playerUUID.toString())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean bankExist(String bankName){
        File bankFile = getBankFile(bankName);
        return bankFile.exists();
    }


    public static double getBalance(Player player) {
        if (!hasAccount(player.getUniqueId())) {
            return 0;
        }
        File bankFile = getPlayerFile(player.getUniqueId());
        YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
        return Double.parseDouble(config.getString("money"));
    }

    public static boolean has(Player player, double amount) {
        if (!hasAccount(player.getUniqueId())) {
            return false;
        }
        return getBalance(player) >= amount;
    }

    public static void withdrawPlayer(double amount, UUID uuid, String bankName) {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getBankFile(bankName));

        int money = configuration.getInt("money");
        double new_money = money - amount;
        Economy economy = Main.getEconomy();
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        configuration.set("money", new_money);
        economy.depositPlayer(player, amount);

        try {
            configuration.save(getBankFile(bankName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void depositPlayer(double amount, UUID playerUUID, String banKName) {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getBankFile(banKName));

        int money = configuration.getInt("money");
        double new_money = money + amount;
        Economy economy = Main.getEconomy();
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);

        economy.withdrawPlayer(player, amount);
        configuration.set("money", new_money);
        try {
            configuration.save(getBankFile(banKName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void createBank(String bankName, Player p) {
        File bankFile = getBankFile(bankName);
        File playerFile = getPlayerFile(p.getUniqueId());

        if (bankFile.exists()) {
            p.sendMessage(ChatColor.RED + "Une banque avec ce nom existe déjà !");
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        YamlConfiguration player_config;
        if (playerFile.exists()) {
            // Player file already exists, load the existing configuration
            player_config = YamlConfiguration.loadConfiguration(playerFile);
        } else {
            player_config = new YamlConfiguration();
            player_config.set("Name",p.getName());
        }
        config.set("money", 0);
        config.set("name", bankName);
        config.set("created_date", new Date());

        player_config.set(bankName, "owner");

        try {
            config.save(bankFile);
            player_config.save(playerFile);
            p.sendMessage(ChatColor.GREEN + "La banque " + bankName + " vient d'être créée avec succès");
        } catch (IOException e) {
            e.printStackTrace();
            p.sendMessage(ChatColor.RED + "Une erreur est survenue lors de la création de la banque." + HELPER_WORDS);
        }
    }


    public static void deleteBank(String bankName, Player p) {
        File file = getBankFile(bankName);
        File playerFile = getPlayerFile(p.getUniqueId());
        YamlConfiguration player_config;
        if (playerFile.exists()) {
            // Player file already exists, load the existing configuration
            player_config = YamlConfiguration.loadConfiguration(playerFile);
        } else {
            player_config = new YamlConfiguration();
        }
        player_config.set(bankName, null);
        try {
            player_config.save(playerFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
            if (file.delete()) {
                p.sendMessage("Banque " + bankName + " supprimée avec succès !");
            } else {
                p.sendMessage("Impossible de supprimer la banque " + bankName + "."+HELPER_WORDS);
            }
    }


    public static double bankBalance(String bankName) {
        if (!bankExist(bankName)) {
            return 0;
        }
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getBankFile(bankName));
        return configuration.getInt("money");
    }


    public static boolean bankHas(String bankName, double amount) {
        if (!bankExist(bankName)) {
            return false;
        }
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getBankFile(bankName));
        return configuration.getInt("money") >= amount;
    }


    public static boolean isBankOwner(String bankName, Player player) {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getPlayerFile(player.getUniqueId()));
        return configuration.getString(bankName).equalsIgnoreCase("owner");
    }


    public static boolean isBankMember(String bankName, Player player){
        File bankFile = getBankFile(bankName);
        if (!bankFile.exists()) {
            Bukkit.getPlayer(player.getUniqueId()).sendMessage(ChatColor.RED+"Cette bank n'existe pas");
            return false;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
        return config.getString(bankName).equalsIgnoreCase("members");
    }


    public static List<String> getBanks(Player p) {
        List<String> banks = new ArrayList<>();
        File[] bankFiles = BANK_FOLDER.listFiles();
        File playerFile = getPlayerFile(p.getUniqueId());
        if (bankFiles != null || playerFile != null) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            for (File bankFile : bankFiles) {
                if (bankFile.isFile()) {
                    String bankName = bankFile.getName();
                    int extensionIndex = bankName.lastIndexOf(".");
                    if (extensionIndex > 0) {
                        bankName = bankName.substring(0, extensionIndex);
                    }
                    if (config.getString(bankName) != null && config.getString(bankName).equalsIgnoreCase("owner")) {
                        banks.add(bankName);
                    }
                }
            }
        }
        return banks;
    }



    public static void log(String playerName, String action) {
        File file = new File(ADMIN_FOLDER + File.separator + "logs.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        String dateString = formatter.format(date);

        List<String> logs = config.getStringList("logs");
        logs.add(dateString + " - " + playerName + " - " + action);

        config.set("logs", logs);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
