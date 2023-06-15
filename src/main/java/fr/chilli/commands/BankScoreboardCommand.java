package fr.chilli.commands;

import fr.chilli.util.SharedAccount;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

public class BankScoreboardCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player){

            Player player = (Player) sender;

            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = manager.getNewScoreboard();

            Objective objective = scoreboard.registerNewObjective("test","dummy", ChatColor.BLUE+"Bank");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            Score score = objective.getScore("Money: "+ SharedAccount.bankBalance("bank"));
            score.setScore(0);

            player.setScoreboard(scoreboard);

        }


        return true;
    }
}