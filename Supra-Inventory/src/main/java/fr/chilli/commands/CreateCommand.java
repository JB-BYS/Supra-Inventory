package fr.chilli.commands;

import fr.chilli.util.DataSaver;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"Seuls les joueurs peuvent utiliser cette commande.");
            return true;
        }

        Player p = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage("Utilisation de la commande : /create [nom_de_la_banque].");
            return true;
        }
        String bankname = args[0];

        if (DataSaver.checkFile(bankname)){
            p.sendMessage(ChatColor.RED+"Cette banque existe déja.");
            return true;
        }

            DataSaver.createBank(p,bankname);

        sender.sendMessage(ChatColor.GREEN+"Tu as créé la banque "+bankname+ChatColor.GREEN+"!");

        return true;
    }
}