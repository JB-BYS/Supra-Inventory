package fr.chilli;

import fr.chilli.commands.*;
import fr.chilli.util.Bank;
import fr.chilli.util.BankGUI;
import fr.chilli.util.UtilsBank;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private Timer timer;
    private TimerTask taxeTask;


    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));

        if (timer != null) {
            timer.cancel();
        }

    }

    @Override
    public void onEnable() {

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPerms api = provider.getProvider();

        }

        File banksFolder = new File(getDataFolder(), "banks");
        if (!banksFolder.exists()) {
            banksFolder.mkdirs();
        }
        File adminFolder = new File(getDataFolder(), "admin");
        if (!adminFolder.exists()) {
            adminFolder.mkdirs();
        }

        getCommand("bank").setExecutor(new BankCommand());
        getCommand("sb").setExecutor(new BankScoreboardCommand());
        getCommand("bank").setTabCompleter(new BankCommandTabCompletion());
        getServer().getPluginManager().registerEvents(new BankGUI(), this);


        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    public static Economy getEconomy() {
        return econ;
    }

}
