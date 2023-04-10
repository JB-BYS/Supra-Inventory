package fr.chilli.commands;

import fr.chilli.util.DataSaver;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuitterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"Seuls les joueurs peuvent utiliser cette commande.");
            return true;
        }

        Player p = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED+"Utilisation de la commande : /quitter [nom_de_la_banque].");
            return true;
        }

        String bankname = args[0];
        int bank = DataSaver.loadMoney(bankname);

        if (!(DataSaver.checkFile(bankname))){
            p.sendMessage(ChatColor.RED+"Cette banque n'existe pas");
            return true;
        }

        if (!(bank == 0)){
            p.sendMessage(ChatColor.RED+"Woups, la banque n'est pas vide, assure toi de vider le compte avant de la supprimer.");
            return true;
        }

        DataSaver.delPerms(p.getName(),bankname);

        sender.sendMessage(ChatColor.GREEN+"Tu ne fais plus partie de la banque "+bankname);

        return true;
    }
}
