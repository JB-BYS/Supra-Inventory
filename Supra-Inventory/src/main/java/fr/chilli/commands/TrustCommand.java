package fr.chilli.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TrustCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"Seuls les joueurs peuvent utiliser cette commande.");
            return true;
        }

        Player p = (Player) sender;

        if (args.length < 2) {
            p.sendMessage(ChatColor.RED+"Utilisation de la commande : /trust [nom_de_la_banque] [joueur].");
            return true;
        }
        String bankname = args[0];
        String nouveau = args[1];

        if (!(DataSaver.checkFile(bankname))){
            p.sendMessage(ChatColor.RED+"Cette banque n'existe pas.");
            return true;
        }

        if (!(DataSaver.checkOwner(p.getName(),bankname))){
            p.sendMessage(ChatColor.RED+"Tu n'es pas le créateur de cette banque.");
            return true;
        }

        DataSaver.addPerms(nouveau,bankname);

        p.sendMessage(ChatColor.GREEN+"Tu as ajouté "+nouveau+" sur la banque "+bankname);

        return true;
    }
}
