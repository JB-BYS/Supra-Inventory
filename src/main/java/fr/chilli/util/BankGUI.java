package fr.chilli.util;

import fr.chilli.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static fr.chilli.util.SharedAccount.getBanks;

public class BankGUI implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() == null && (event.getView().getTitle().equals("Mes Banques") || event.getView().getTitle().startsWith("Banque : "))) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            // Traitement de l'action en fonction de l'item cliqué
            if (clickedItem != null) {
                Material clickedMaterial = clickedItem.getType();

                switch (clickedMaterial) {
                    case CHEST:
                        // Ouverture du menu de la banque
                        openBankMenu(player, clickedItem.getItemMeta().getDisplayName());
                        break;
                    case GOLD_INGOT:
                        // Action pour "Déposer de l'argent"
                        player.sendMessage("Fonctionnalité de dépôt à implémenter");
                        break;
                    case GREEN_SHULKER_BOX:
                        // Action pour "Retirer de l'argent"
                        player.sendMessage("Veuillez saisir le montant que vous souhaitez retirer :");
                        player.closeInventory();

                        // Création d'un nouvel objet Conversation
                        ConversationFactory factory = new ConversationFactory(Main.getPlugin(Main.class)); // Remplacez 'plugin' par votre instance de plugin appropriée

                        // Création d'un nouvel objet Conversation avec un prompt pour saisir le montant
                        Conversation conv = factory.withFirstPrompt(new WithdrawAmountPrompt()).buildConversation(player);

                        // Début de la conversation
                        conv.begin();
                        break;
                    case RED_SHULKER_BOX:
                        // Action pour "Retirer de l'argent"
                        break;
                    case BARRIER:
                        // Action pour "Fermer"
                        player.closeInventory();
                        break;
                    default:
                        // Action par défaut si le matériau ne correspond à aucun cas
                        break;
                }
            }

        }
    }

    public void openBankMenu(Player player, String bankName) {
        // Récupération du solde de la banque
        double balance = SharedAccount.bankBalance(bankName);

        Inventory menu = Bukkit.createInventory(null, 45, "Banque : " + bankName);

        // Remplissage des cases avec des vitres noires
        ItemStack blackGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, "");
        int[] blackGlassSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43};
        for (int slot : blackGlassSlots) {
            menu.setItem(slot, blackGlass);
        }

        // Création de l'élément affichant le solde de la banque
        ItemStack balanceItem = createItem(Material.GOLD_INGOT, ChatColor.GREEN + "Solde : " + balance);
        menu.setItem(13, balanceItem);

        ItemStack depositItem = createItem(Material.GREEN_SHULKER_BOX, ChatColor.GREEN + "Clique pour déposer de l'argent");
        menu.setItem(20, depositItem);

        ItemStack WithdrawItem = createItem(Material.RED_SHULKER_BOX, ChatColor.GREEN + "Clique pour retirer de l'argent");
        menu.setItem(24, WithdrawItem);

        // Création du bouton "Fermer"
        ItemStack closeButton = createItem(Material.BARRIER, ChatColor.RED + "Fermer");
        menu.setItem(44, closeButton);

        // Ouverture du menu de la banque pour le joueur
        player.openInventory(menu);
    }

    public static void openBankMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 54, "Mes Banques");

        List<String> banks = getBanks(player);

        // Remplissage des cases avec des vitres noires
        ItemStack blackGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, "");
        int[] blackGlassSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
        for (int slot : blackGlassSlots) {
            menu.setItem(slot, blackGlass);
        }
        Set<Integer> blackGlassSlotsSet = new HashSet<>();
        for (int slot : blackGlassSlots) {
            blackGlassSlotsSet.add(slot);
        }
        int exp = 0;
        for (int i = 0; exp < banks.size();) {
            if (!blackGlassSlotsSet.contains(i)) {
                String bankName = banks.get(exp);
                ItemStack bankItem = createItem(Material.CHEST, ChatColor.BLUE + bankName);
                menu.setItem(i, bankItem);
                exp++;
            }
            i++;
        }

        // Création du bouton "Fermer"
        ItemStack closeButton = createItem(Material.BARRIER, ChatColor.RED + "Fermer");
        menu.setItem(53, closeButton);

        player.openInventory(menu);
    }

    private static ItemStack createItem(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }
}
