package fr.chilli.commands;

import fr.chilli.util.DataSaver;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SoldeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"Seuls les joueurs peuvent utiliser cette commande.");
            return true;
        }

        Player p = (Player) sender;
        String name = p.getName();

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED+"Utilisation de la commande : /solde [nom_de_la_banque].");
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

        p.sendMessage(ChatColor.GREEN+"Il y a "+bank+" Pp, sur la banque "+bankname);
        return true;
    }
}