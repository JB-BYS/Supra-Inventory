package fr.chilli.util;

import com.google.common.reflect.ClassPath;
import fr.chilli.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Bank {

    public static final String CURRENCY_NAME = "Pp";
    private static final File DATA_FOLDER = new File(Main.getPlugin(Main.class).getDataFolder(), "banks");
    private static final File ADMIN_FOLDER = new File(Main.getPlugin(Main.class).getDataFolder(), "admin");

    private static File getFile(UUID creatorUUID, String bankName) {
        return new File(DATA_FOLDER, bankName + ".yml");
    }

    public static void create(String bankName, UUID creatorUUID) {
        File bankFile = getFile(creatorUUID,bankName);
        if (bankFile.exists()) {
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        List<String> playerUUIDs = new ArrayList<>();
        config.set("owner", String.valueOf(creatorUUID));
        config.set("money", 0);
        config.set("name", bankName);
        config.set("created_date", new Date());


        try {
            config.save(bankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(String bankName, UUID creatorUUID) {
        File file = getFile(creatorUUID,bankName);
        file.delete();
        if (file.delete()) {
            System.out.println("Banque " + bankName + " supprimée avec succès !");
        } else {
            System.out.println("Impossible de supprimer la banque " + bankName + ".");
        }
    }
    public static boolean isOwner(String bankName, UUID playerUUID) {
        File bankDir = DATA_FOLDER;

        for (File bankFile : bankDir.listFiles()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
            if (bankName.equalsIgnoreCase(config.getString("name")) && playerUUID.toString().equalsIgnoreCase(config.getString("owner"))){
                return true;
            }
        }
        return false;
    }
    public static void spawnGreenCircleParticles(Player player, Color color) {
        // Define the color for the particles

        // Calculate the number of particles to spawn
        int particleCount = 25;
        double increment = (2 * Math.PI) / particleCount;

        // Spawn the particles in a circle above the player
        Location loc = player.getLocation().add(0, 2, 0);
        World world = player.getWorld();
        for (int i = 0; i < particleCount; i++) {
            double angle = i * increment;
            double x = Math.cos(angle) * 1.5;
            double z = Math.sin(angle) * 1.5;
            Vector offset = new Vector(x, 0, z);
            Location particleLoc = loc.clone().add(offset);
            world.spawnParticle(Particle.REDSTONE, particleLoc, 0, new Particle.DustOptions(color, 1));
        }
    }



    public static int getTrustLevel(String bankName, UUID playerUUID) {
        File bankFile = getFile(playerUUID, bankName);
        YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);

        if (bankConfig.contains("trust.trust_lvl1") && bankConfig.getStringList("trust.trust_lvl1").contains(playerUUID.toString())) {
            return 1;
        } else if (bankConfig.contains("trust.trust_lvl2") && bankConfig.getStringList("trust.trust_lvl2").contains(playerUUID.toString())) {
            return 2;
        } else if (bankConfig.contains("trust.trust_lvl3") && bankConfig.getStringList("trust.trust_lvl3").contains(playerUUID.toString())) {
            return 3;
        } else if (bankConfig.contains("trust.trust_lvl4") && bankConfig.getStringList("trust.trust_lvl4").contains(playerUUID.toString())) {
            return 4;
        } else {
            return 0; // joueur non trouvé dans la banque
        }
    }



    public String getOwner(String bankName, UUID creatorUUID) {
        File bankFile = getFile(creatorUUID,bankName);
        if (!bankFile.exists()) {
            return null;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
        return config.getString("owner");
    }

    public void setOwner(Player newOwnerName, Player oldOwnerName, String bankName) {

        File bankFile = getFile(oldOwnerName.getUniqueId(),bankName);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);

        if (config.getString("owner").equalsIgnoreCase(oldOwnerName.getUniqueId().toString())){

            if (Bukkit.getOfflinePlayer(newOwnerName.getUniqueId()) == null) {
                oldOwnerName.sendMessage(ChatColor.RED + "Joueur inconnu !");
                return;
            }
            config.set("owner", newOwnerName.getUniqueId().toString());
        }

        try {
            config.save(bankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void withdraw(double amount, UUID uuid, String bankName) {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getFile(uuid, bankName));

        int money = configuration.getInt("money");
        double new_money = money - amount;
        Economy economy = Main.getEconomy();
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        configuration.set("money", new_money);
        economy.withdrawPlayer(player, amount);

        try {
            configuration.save(getFile(uuid, bankName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deposit(double amount, UUID playerUUID, String banKName) {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(getFile(playerUUID, banKName));

        int money = configuration.getInt("money");
        double new_money = money + amount;
        Economy economy = Main.getEconomy();
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);

        economy.withdrawPlayer(player, amount);
        configuration.set("money", new_money);
        try {
            configuration.save(getFile(playerUUID, banKName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static double balance(Player player, String BankName) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(getFile(player.getUniqueId(), BankName));
        double money = config.getDouble("money");
        return money;
    }

    public static void trust(String bankName, UUID playerUUID, int lvl) {
        if (playerUUID == null) {
            return;
        }

        File bankFile = getFile(playerUUID, bankName);
        YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);


        }


    public static void untrust(String bankName, UUID playerUUID) {
        if (playerUUID == null) {
            return;
        }

        File bankFile = getFile(playerUUID,bankName);
        YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);
        List<String> memberList = bankConfig.getStringList("trust");
        memberList.remove(playerUUID.toString());
        bankConfig.set("trust", memberList);
        try {
            bankConfig.save(bankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isTrusted(UUID playerUUID, String bankName) {
        File bankFile = getFile(playerUUID,bankName);
        if (bankFile != null) {
            YamlConfiguration bankConfig = YamlConfiguration.loadConfiguration(bankFile);
            List<String> memberList = bankConfig.getStringList("trust");
            return memberList.contains(playerUUID.toString());
        }
        return false;
    }

    public static boolean bankNameAvailable(String bankName) {
        File bankDir = DATA_FOLDER;

        for (File bankFile : bankDir.listFiles()) {
            if (bankFile.getName().equals(bankName+".yml")) {
                return false;
            }
        }
        return true;
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
