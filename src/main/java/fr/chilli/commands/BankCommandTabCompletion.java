package fr.chilli.commands;

import fr.chilli.util.SharedAccount;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class BankCommandTabCompletion implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        if(args.length ==1){
            List<String> commands = Arrays.asList("create","delete","withdraw","deposit","balance");
            return commands;
        }

        if(args.length == 2) {
            switch (args[0]) {
                case "delete":
                    return SharedAccount.getBanks(player);
                case "deposit":
                    return SharedAccount.getBanks(player);
                case "withdraw":
                    return SharedAccount.getBanks(player);
                case "solde":
                    return SharedAccount.getBanks(player);
            }
        }

        if(args.length == 3) {
            switch (args[0]) {
                case "deposit":
                    List<String> number = Arrays.asList("1","10","100","1000","10000");
                    return number;
                case "withdraw":
                    List<String> numbers = Arrays.asList("1","10","100","1000","10000");
                    return numbers;
            }
        }

        return null;
    }
}
