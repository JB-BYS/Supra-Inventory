package fr.chilli.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class ConfirmationGUI implements Listener {

    public enum ConfirmationType {
        DEPOSIT,
        WITHDRAW
    }

    public static void openConfirmationMenu(Player player, double amount, ConfirmationType type) {
        Inventory menu = Bukkit.createInventory(null, 27, "Confirmation");

        ItemStack confirmItem = createItem(Material.LIME_DYE, ChatColor.GREEN + "Confirmer", amount, type);
        ItemStack cancelItem = createItem(Material.RED_DYE, ChatColor.RED + "Annuler", amount, type);

        menu.setItem(11, confirmItem);
        menu.setItem(15, cancelItem);

        player.openInventory(menu);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event, ConfirmationType type) {
        if (event.getView().getTitle().equals("Confirmation")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            String itemName = clickedItem.getItemMeta().getDisplayName();

            if (itemName.equals(ChatColor.GREEN + "Confirmer")) {
                if (type == ConfirmationType.WITHDRAW){
                    
                }
                player.sendMessage("Retrait effectué avec succès !");
            } else if (itemName.equals(ChatColor.RED + "Annuler")) {
                player.sendMessage("Retrait annulé.");
            }

            player.closeInventory();
        }
    }

    private static ItemStack createItem(Material material, String displayName, double amount, ConfirmationType type) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "Montant : " + amount + " $"));
        item.setItemMeta(meta);
        return item;
    }
}
