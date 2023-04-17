package fr.chilli.commands;

import fr.chilli.Main;
import fr.chilli.util.Bank;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BankCommand implements CommandExecutor {

    Economy economy = Main.getEconomy();
    Bank bank;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cette commande ne peut être exécutée que par un joueur !");
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("bank")) {
            if (args.length == 0) {
                player.sendMessage("Utilisation : /bank <create|delete|quit|withdraw|deposit|solde|trust|untrust|history>");
                return true;
            }
//----------------------------------------------------------------------------------------------------------------------

            if (args[0].equalsIgnoreCase("create")) {
                if (args.length != 2) {
                    player.sendMessage("Utilisation : /bank create <nom de la banque>");
                    return true;
                }

                String bankName = args[1];

                if (!bank.bankNameAvailable(bankName)) {
                    player.sendMessage("Une banque avec ce nom existe déjà !");
                    return true;
                }

                bank.create(bankName, player.getUniqueId());
                player.sendMessage("La banque " + bankName + " a été créée avec succès !");

            }
//----------------------------------------------------------------------------------------------------------------------

            else if (args[0].equalsIgnoreCase("delete")) {
                if (args.length != 2) {
                    player.sendMessage("Utilisation : /bank delete <nom de la banque>");
                    return true;
                }

                String bankName = args[1];

                if (!bank.getOwnedBankByName(bankName)) {
                    player.sendMessage("Cette banque ne t'appartient pas");
                    return true;
                }

                if (!bank.bankNameAvailable(bankName)) {
                    player.sendMessage("Une banque avec ce nom existe déjà !");
                    return true;
                }

                double balance = bank.balance(bankName);
                if (balance > 0) {
                    EconomyResponse response = economy.depositPlayer(player, balance);

                    if (!response.transactionSuccess()) {
                        player.sendMessage("Une erreur est survenue lors du remboursement de votre banque !");
                        return true;
                    }
                }
                bank.delete(bankName);
                player.sendMessage("Votre banque a été supprimée avec succès et " + balance + " " + Bank.CURRENCY_NAME + " ont été remboursés sur votre compte !");

//----------------------------------------------------------------------------------------------------------------------
            } else if (args[0].equalsIgnoreCase("quit")) {
                if (args.length != 2) {
                    player.sendMessage("Utilisation : /bank quit <Nom de la banque>");
                    return true;
                }

                String bankName = args[1];

                if (!bank.bankNameAvailable(bankName)) {
                    player.sendMessage("Une banque avec ce nom existe déjà !");
                    return true;
                }

                if (bank.getOwnedBankByName(bankName)) {
                    player.sendMessage("Tu es propriétaire de cette banque, fait /bank trust <Nom d'un joueur> avant de la quitter");
                    return true;
                }
                bank.untrust(bankName,player.getUniqueId());
//----------------------------------------------------------------------------------------------------------------------
            } else if (args[0].equalsIgnoreCase("withdraw")) {
                if (args.length != 3) {
                    player.sendMessage("Utilisation : /bank withdraw <Nom de la banque> <montant>");
                    return true;
                }

                String bankName = args[1];
                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Montant invalide !");
                    return true;
                }

                if (!bank.getOwnedBankByName(bankName)) {
                    player.sendMessage("Cette banque ne t'appartient pas");
                    return true;
                }

                if (!bank.bankNameAvailable(bankName)) {
                    player.sendMessage("Une banque avec ce nom existe déjà !");
                    return true;
                }

                if (amount <= 0) {
                    player.sendMessage("Montant invalide !");
                    return true;
                }
                if (bank.balance(bankName) < amount) {
                    player.sendMessage("Solde insuffisant !");
                    return true;
                }

                bank.withdraw(amount, bankName);
                player.sendMessage("Retrait de " + amount + " " + Bank.CURRENCY_NAME + " effectué !");
                return true;
            }
//----------------------------------------------------------------------------------------------------------------------
            else if (args[0].equalsIgnoreCase("deposit")) {
                if (args.length != 3) {
                    player.sendMessage("Utilisation : /bank deposit <Nom de la banque> <montant>");
                    return true;
                }

                String bankName = args[1];
                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Montant invalide !");
                    return true;
                }

                if (!bank.getOwnedBankByName(bankName)) {
                    player.sendMessage("Cette banque ne t'appartient pas");
                    return true;
                }

                if (!bank.bankNameAvailable(bankName)) {
                    player.sendMessage("Une banque avec ce nom existe déjà !");
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

                bank.deposit(amount, bankName);
                player.sendMessage("Retrait de " + amount + " " + Bank.CURRENCY_NAME + " effectué !");
                return true;
            }
//----------------------------------------------------------------------------------------------------------------------

            else if (args[0].equalsIgnoreCase("balance")) {
                if (args.length != 2) {
                    player.sendMessage("Utilisation : /bank balance <Nom de la banque>");
                    return true;
                }
                String bankName = args[1];

                if (!bank.getOwnedBankByName(bankName)) {
                    player.sendMessage("Cette banque ne t'appartient pas");
                    return true;
                }

                if (!bank.bankNameAvailable(bankName)) {
                    player.sendMessage("Une banque avec ce nom existe déjà !");
                    return true;
                }
                player.sendMessage("Vous avez" + " " + bank.balance(bankName) + Bank.CURRENCY_NAME + " sur la banque " + bankName);
                return true;
            }
//----------------------------------------------------------------------------------------------------------------------
            else if (args[0].equalsIgnoreCase("trust")) {
                if (args.length != 3) {
                    player.sendMessage("Utilisation : /bank trust <Nom de la banque> <joueur>");
                    return true;
                }

                String bankName = args[1];
                UUID playerName = UUID.fromString(args[2]);

                if (!bank.getOwnedBankByName(bankName)) {
                    player.sendMessage("Cette banque ne t'appartient pas");
                    return true;
                }

                if (!bank.bankNameAvailable(bankName)) {
                    player.sendMessage("Une banque avec ce nom existe déjà !");
                    return true;
                }

                if (!bank.isTrusted(playerName, bankName)){
                    bank.trust(bankName,playerName);
                    player.sendMessage("Vous avez ajouté "+playerName + " à votre bank" + bankName);
                }
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

            if (!bank.getOwnedBankByName(bankName)) {
                player.sendMessage("Cette banque ne t'appartient pas");
                return true;
            }

            if (!bank.bankNameAvailable(bankName)) {
                player.sendMessage("Une banque avec ce nom existe déjà !");
                return true;
            }

            if (bank.isTrusted(playerName, bankName)){
                bank.untrust(bankName,playerName);
                player.sendMessage("Vous avez retiré "+playerName + " de votre bank" + bankName);
            }
        }
        return true;
    }
}



