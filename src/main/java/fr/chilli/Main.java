package fr.chilli;

import fr.chilli.commands.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        Objects.requireNonNull(this.getCommand("transfer")).setExecutor(new TransfertCommand());
        Objects.requireNonNull(this.getCommand("create")).setExecutor(new CreateCommand());
        Objects.requireNonNull(this.getCommand("delete")).setExecutor(new DeleteCommand());
        Objects.requireNonNull(this.getCommand("retire")).setExecutor(new RetirerCommand());
        Objects.requireNonNull(this.getCommand("solde")).setExecutor(new SoldeCommand());
        Objects.requireNonNull(this.getCommand("trust")).setExecutor(new TrustCommand());
        Objects.requireNonNull(this.getCommand("untrust")).setExecutor(new UntrustCommand());
        Objects.requireNonNull(this.getCommand("quitter")).setExecutor(new QuitterCommand());
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
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
        return true;
    }


    public static Economy getEconomy() {
        return econ;
    }

}
