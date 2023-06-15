package fr.chilli.commands;

import fr.chilli.Main;
import fr.chilli.util.BankGUI;
import fr.chilli.util.SharedAccount;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
                SharedAccount.createBank(bankName, player);
            }
//----------------------------------------------------------------------------------------------------------------------
            else if (args[0].equalsIgnoreCase("delete")) {
                if (args.length != 2) {
                    player.sendMessage("Utilisation : /bank delete <nom de la banque>");
                    return true;
                }
                String bankName = args[1];
                SharedAccount.deleteBank(bankName,player);
//----------------------------------------------------------------------------------------------------------------------
            } else if (args[0].equalsIgnoreCase("quit")) {
                if (args.length != 2) {
                    player.sendMessage("Utilisation : /bank quit <Nom de la banque>");
                    return true;
                }
                String bankName = args[1];

                //SharedAccount.quit
//----------------------------------------------------------------------------------------------------------------------
            } else if (args[0].equalsIgnoreCase("setOwner")) {
                if (args.length != 3) {
                    player.sendMessage("Utilisation : /bank setOwner <Nom de la banque> <Nom du Joueur>");
                    return true;
                }
                String bankName = args[1];

                //SharedAccount.setOwner(player, p, bankName);
//----------------------------------------------------------------------------------------------------------------------
            } else if (args[0].equalsIgnoreCase("withdraw")) {
                if (args.length != 3) {
                    player.sendMessage("Utilisation : /bank withdraw <Nom de la banque> <montant>");
                    return true;
                }

                String bankName = args[1];

                if (!SharedAccount.isBankOwner(bankName,player)) {
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
                if (!SharedAccount.bankHas(bankName,amount)) {
                    player.sendMessage("Solde insuffisant !");
                    return true;
                }

                SharedAccount.withdrawPlayer(amount, player.getUniqueId(), bankName);
                player.sendMessage("Retrait de " + amount + " " + SharedAccount.CURRENCY_NAME + " effectué !");
                SharedAccount.log(player.getName(), player + " vient de virer " + amount + " vers son compte personnel");
            }
//----------------------------------------------------------------------------------------------------------------------
            else if (args[0].equalsIgnoreCase("deposit")) {
                if (args.length != 3) {
                    player.sendMessage("Utilisation : /bank deposit <Nom de la banque> <montant>");
                    return true;
                }

                String bankName = args[1];
                Economy economy = Main.getEconomy();
                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Montant invalide !");
                    return true;
                }

                if (!SharedAccount.isBankOwner(bankName, player)) {
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

                SharedAccount.depositPlayer(amount, player.getUniqueId(), bankName);
                player.sendMessage("Envoie de " + amount + " " + SharedAccount.CURRENCY_NAME + " effectué !");
                SharedAccount.log(player.getName(), player + " vient de virer " + amount + " vers la banque " + bankName);
                return true;
            }
//----------------------------------------------------------------------------------------------------------------------

            else if (args[0].equalsIgnoreCase("gui")) {
                BankGUI.openBankMenu(player);
                return true;
            }
        }
        return true;
    }
}
