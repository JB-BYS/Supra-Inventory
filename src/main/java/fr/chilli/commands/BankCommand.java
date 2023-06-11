package fr.chilli.commands;

import fr.chilli.Main;
import fr.chilli.util.Bank;
import fr.chilli.util.BankGUI;
import fr.chilli.util.UtilsBank;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.data.type.Switch;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.UUID;

public class BankCommand implements CommandExecutor {



    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
       if (!(sender instanceof Player)) {
           sender.sendMessage("Cette commande ne peut être exécutée que par un joueur !");
           return true;
       }

        Player player = (Player) sender;


        if (cmd.getName().equalsIgnoreCase("bank")) {
            if (args.length == 0) {
                player.sendMessage("Utilisation : " + ChatColor.GREEN + "/bank <create|delete|quit|withdraw|deposit|solde|trust|untrust|history>");
                return true;
            }
//----------------------------------------------------------------------------------------------------------------------
            if (args[0].equalsIgnoreCase("create")) {
                if (args.length != 2) {
                    player.sendMessage("Utilisation : /bank create <nom de la banque>");
                    return true;
                }
                String bankName = args[1];
                UtilsBank.create(bankName, player);
            }
//----------------------------------------------------------------------------------------------------------------------
            else if (args[0].equalsIgnoreCase("delete")) {
                if (args.length != 2) {
                    player.sendMessage("Utilisation : /bank delete <nom de la banque>");
                    return true;
                }
                String bankName = args[1];
                UtilsBank.delete(bankName, player);
//----------------------------------------------------------------------------------------------------------------------
            } else if (args[0].equalsIgnoreCase("quit")) {
                if (args.length != 2) {
                    player.sendMessage("Utilisation : /bank quit <Nom de la banque>");
                    return true;
                }
                String bankName = args[1];

                //UtilsBank.quit
//----------------------------------------------------------------------------------------------------------------------
            } else if (args[0].equalsIgnoreCase("setOwner")) {
                if (args.length != 3) {
                    player.sendMessage("Utilisation : /bank setOwner <Nom de la banque> <Nom du Joueur>");
                    return true;
                }
                String bankName = args[1];
                Player p = UtilsBank.getPlayer(args[2]);

                UtilsBank.setOwner(player, p, bankName);
//----------------------------------------------------------------------------------------------------------------------
            } else if (args[0].equalsIgnoreCase("withdraw")) {
                if (args.length != 3) {
                    player.sendMessage("Utilisation : /bank withdraw <Nom de la banque> <montant>");
                    return true;
                }

                String bankName = args[1];

                if (!Bank.isOwner(bankName, player.getUniqueId())) {
                    player.sendMessage("Cette banque ne t'appartient pas");
                    return true;
                }

                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Montant invalide !");
                    return true;
                }

                if (amount <= 0) {
                    player.sendMessage("Montant invalide !");
                    return true;
                }
                if (Bank.balance(player, bankName) < amount) {
                    player.sendMessage("Solde insuffisant !");
                    return true;
                }

                Bank.withdraw(amount, player.getUniqueId(), bankName);
                player.sendMessage("Retrait de " + amount + " " + Bank.CURRENCY_NAME + " effectué !");
                Bank.log(player.getName(), player + " vient de virer " + amount + " vers son compte personnel");
            }
//----------------------------------------------------------------------------------------------------------------------
            else if (args[0].equalsIgnoreCase("deposit")) {
                if (args.length != 3) {
                    player.sendMessage("Utilisation : /bank deposit <Nom de la banque> <montant>");
                    return true;
                }

                String bankName = args[1];
                Economy economy = Main.getEconomy();

                System.out.println(Bank.getTrustLevel(bankName, player.getUniqueId()));

                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Montant invalide !");
                    return true;
                }

                if (!Bank.isOwner(bankName, player.getUniqueId())) {
                    player.sendMessage("Cette banque ne t'appartient pas");
                    return true;
                }
                if (amount <= 0) {
                    player.sendMessage("Montant invalide !");
                    return true;
                }

                if (economy.getBalance(player) < amount) {
                    player.sendMessage("Solde insuffisant !");
                    return true;
                }

                Bank.deposit(amount, player.getUniqueId(), bankName);
                player.sendMessage("Envoie de " + amount + " " + Bank.CURRENCY_NAME + " effectué !");
                Bank.log(player.getName(), player + " vient de virer " + amount + " vers la banque " + bankName);
                return true;
            }
//----------------------------------------------------------------------------------------------------------------------

            else if (args[0].equalsIgnoreCase("balance")) {
                if (args.length != 2) {
                    player.sendMessage("Utilisation : /bank balance <Nom de la banque>");
                    return true;
                }
                String bankName = args[1];

                if (!Bank.isOwner(bankName, player.getUniqueId())) {
                    player.sendMessage("Cette banque ne t'appartient pas");
                    return true;
                }

                if (!Bank.bankNameAvailable(bankName)) {
                    player.sendMessage("Une banque avec ce nom existe déjà !");
                    return true;
                }
                player.sendMessage("Vous avez" + " " + Bank.balance(player, bankName) + Bank.CURRENCY_NAME + " sur la banque " + bankName);
                Bank.log(player.getName(), player + " vient de checker le solde de la banque " + bankName);
                return true;
            }
//----------------------------------------------------------------------------------------------------------------------
            else if (args[0].equalsIgnoreCase("trust")) {
                if (args.length != 4) {
                    player.sendMessage("Utilisation : /bank trust <Nom de la banque> <joueur> <level>");
                    return true;
                }

                String bankName = args[1];
                OfflinePlayer playertrusted = Bukkit.getOfflinePlayer(args[2]);
                Integer lvl = Integer.valueOf(args[3]);

                if (!Bank.isOwner(bankName, player.getUniqueId())) {
                    player.sendMessage("Cette banque ne t'appartient pas");
                    return true;
                }

                if (!Bank.isTrusted(playertrusted.getUniqueId(), bankName)) {
                    Bank.trust(bankName, playertrusted.getUniqueId(), lvl);
                    player.sendMessage("Vous avez ajouté " + playertrusted + " à votre bank" + bankName);
                    Bank.log(player.getName(), player + " vient d'ajouter " + playertrusted + " à la banque " + bankName);
                }
            }
//----------------------------------------------------------------------------------------------------------------------
            else if (args[0].equalsIgnoreCase("untrust")) {
                if (args.length != 3) {
                    player.sendMessage("Utilisation : /bank untrust <Nom de la banque> <joueur>");
                    return true;
                }

                String bankName = args[1];
                UUID playerName = UUID.fromString(args[2]);

                if (!Bank.isOwner(bankName, player.getUniqueId())) {
                    player.sendMessage("Cette banque ne t'appartient pas");
                    return true;
                }

                if (!Bank.bankNameAvailable(bankName)) {
                    player.sendMessage("Une banque avec ce nom existe déjà !");
                    return true;
                }

                if (Bank.isTrusted(playerName, bankName)) {
                    Bank.untrust(bankName, playerName);
                    player.sendMessage("Vous avez retiré " + playerName + " de votre bank" + bankName);
                    Bank.log(player.getName(), player + " vient de retirer " + playerName + " de la banque " + bankName);
                }
                //------------------------------------------------------------------------------------------------------------------
            } else if (args[0].equalsIgnoreCase("add")) {
                if (args.length != 3) {
                    player.sendMessage("Utilisation : /bank add <Nom de la banque> <joueur>");
                    return true;
                }

                String bankName = args[1];
                UUID playerName = UUID.fromString(args[2]);


                player.sendMessage("Vous avez retiré " + playerName + " de votre bank" + bankName);
                Bank.log(player.getName(), player + " vient de retirer " + playerName + " de la banque " + bankName);
            }
            //------------------------------------------------------------------------------------------------------------------
            else if (args[0].equalsIgnoreCase("clear")) {
                if (args.length != 2) {
                    player.sendMessage("Utilisation : /bank clear <rayon>");
                    return true;
                }

                Location loc = player.getLocation();
                Integer rayon = Integer.valueOf(args[1]);

                UtilsBank.clearItemsInRadius(loc,rayon);
            }
            else if (args[0].equalsIgnoreCase("perms")){
                if (args.length != 4){
                    player.sendMessage("Urtilisation de la commande : /bank perms add/remove <bank> <perms>");
                    return true;
                }
                String argument = args[3];
                String bank = args[2];
                Player target = Bukkit.getPlayer("chilli_pepper");
                if (args[1].equalsIgnoreCase("add")){
                    UtilsBank.add_to_bank(bank,player,target,argument);
                    System.out.println("add perms");
                }
                else {
                    UtilsBank.removePermission(player.getUniqueId(),bank,argument);
                }

            }
            else if (args[0].equalsIgnoreCase("bite")) {
                BankGUI.openBankMenu(player);
            }
        }
        return true;
    }
}



