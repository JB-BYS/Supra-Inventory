package fr.chilli.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BankGUI implements Listener {


    public static void openBankMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 54, "Mes Banques");

        // Création des items du menu
        ItemStack checkBalanceItem = createItem(Material.CHEST, "Consulter le solde");
        ItemStack depositItem = createItem(Material.GOLD_INGOT, "Déposer de l'argent");
        ItemStack withdrawItem = createItem(Material.REDSTONE, "Retirer de l'argent");

        // Ajout des items au menu
        menu.setItem(2, checkBalanceItem);
        menu.setItem(4, depositItem);
        menu.setItem(6, withdrawItem);

        // Ouverture du menu pour le joueur
        player.openInventory(menu);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() == null && event.getView().getTitle().equals("Banque")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            // Traitement de l'action en fonction de l'item cliqué
            if (clickedItem.getItemMeta().getDisplayName().equals("Consulter le solde")) {
                player.sendMessage("Solde actuel : " + "1000");
            } else if (clickedItem.getItemMeta().getDisplayName().equals("Déposer de l'argent")) {
                player.sendMessage("Fonctionnalité de dépôt à implémenter");
            } else if (clickedItem.getItemMeta().getDisplayName().equals("Retirer de l'argent")) {
                player.sendMessage("Fonctionnalité de retrait à implémenter");
            }
        }
    }

    private static ItemStack createItem(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }
}
