package fr.chilli.commands;

import fr.chilli.Main;
import fr.chilli.util.DataSaver;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RetirerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Economy economy = Main.getEconomy();

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"Seuls les joueurs peuvent utiliser cette commande.");
            return true;
        }

        Player p = (Player) sender;
        String name =p.getName();
        double amount;

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED+"Utilisation de la commande : /retire [nom_de_la_banque] [montant].");
            return true;
        }
        String bankname = args[0];
        int bank = DataSaver.loadMoney(bankname);

        if (!(DataSaver.checkFile(bankname))){
            p.sendMessage(ChatColor.RED+"Cette banque n'existe pas.");
            return true;
        }
        if (!(DataSaver.checkPerms(name,bankname))){
            p.sendMessage(ChatColor.RED+"Tu n'es pas autorisé à utiliser cette banque.");
            return true;
        }

        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED+"Le montant spécifié n'est pas valide.");
            return true;
        }

        if (amount < 0) {
            sender.sendMessage(ChatColor.RED+"Le montant ne peut pas être négatif.");
            return true;
        }

        if (amount > bank) {
            sender.sendMessage(ChatColor.RED+"Tu n'as pas assez d'argent sur ta banque pour pouvoir retirer cette somme.");
            return true;
        }

            DataSaver.retirerMoney(amount,bankname);
            economy.depositPlayer(p,amount);

        p.sendMessage(ChatColor.GREEN+"Tu as retiré "+amount+" de la banque "+bankname);

        return true;
    }
}