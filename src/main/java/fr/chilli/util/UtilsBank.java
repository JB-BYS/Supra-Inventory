package fr.chilli.util;

import fr.chilli.Main;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.query.QueryOptions;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle.memberList;

public class UtilsBank {

    public static final String CURRENCY_NAME = ChatColor.YELLOW + "Pp";
    public static final String HELPER_WORDS = ChatColor.RED + " Si le problème subsiste veuillez contacter un admin";
    private static final File DATA_FOLDER = new File(Main.getPlugin(Main.class).getDataFolder(), "banks");

    private static File getFile(String bankName) {
        return new File(DATA_FOLDER, bankName + ".yml");
    }

    //----------------------------------------------------------------------------------------------------------------------
    public static void create(String bankName, Player p) {
        File bankFile = getFile(String.valueOf(p.getUniqueId()));
        if (bankFile.exists()) {
            p.sendMessage(ChatColor.RED+"Une banque avec ce nom existe déjà !");
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        config.set("money", 0);
        config.set("name", bankName);
        config.set("created_date", new Date());

        try {
            config.save(String.valueOf(p.getUniqueId()));
            p.sendMessage(ChatColor.GREEN+"La banque "+bankName+" vient d'être crée avec succès");
        } catch (IOException e) {
            e.printStackTrace();
            p.sendMessage(ChatColor.RED+"Une erreur est survenue lors de la création de la banque."+HELPER_WORDS);
        }
        Bank.spawnGreenCircleParticles(p, Color.GREEN);
    }

    //----------------------------------------------------------------------------------------------------------------------
    public static void delete(String bankName, Player p) {

        if (!isOwner(bankName, p)) {
            p.sendMessage("Cette banque ne t'appartient pas");
            return;
        }
        Economy economy = Main.getEconomy();
        double balance = Bank.balance(p, bankName);
        if (balance > 0) {
            EconomyResponse response = economy.depositPlayer(p, balance);

            if (!response.transactionSuccess()) {
                p.sendMessage("Une erreur est survenue lors du remboursement de votre banque !" + HELPER_WORDS);
                return;
            }
        }
        File file = getFile(bankName);
        file.delete();
        if (file.delete()) {
            p.sendMessage("Impossible de supprimer la banque " + bankName + "." + HELPER_WORDS);
        } else {
            p.sendMessage("Votre banque a été supprimée avec succès et " + balance + " " + CURRENCY_NAME + " ont été remboursés sur votre compte !");
            Bank.log(p.getName(), "Supression de la bank " + bankName);
        }
    }
    //----------------------------------------------------------------------------------------------------------------------

    public static void add_to_bank(String bankName, Player sender,Player player, String perms) {
        File bankFile = getFile(bankName);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);

        UUID playerUUID = player.getUniqueId();
        UUID senderUUID = sender.getUniqueId();

        if (!perms.equals("inside") && !perms.equals("send") && !perms.equals("withdraw") && !perms.equals("manage_player")) {
            return; // La valeur de perms n'est pas valide, retourne la fonction
        }

        List <String> list = getPlayerPermissions(senderUUID);
        List <String> playerlist = getPlayerPermissions(playerUUID);

        if (playerlist.contains(perms)){
            sender.sendMessage("ce joueur à déja cette permission");
            return;
        }

        if (!(list.contains("chef") || list.contains("manage player"))) {
            sender.sendMessage("tu n'as pas la perms");
            return;
        }

        switch (perms) {
            case "inside":
                addPermission(playerUUID,bankName,"inside");
                break;
            case "send":
                addPermission(playerUUID,bankName,"send");
                break;
            case "withdraw":
                addPermission(playerUUID,bankName,"withdraw");
                break;
            case "manage_player":
                addPermission(playerUUID,bankName,"manage_player");
                break;
            default:
                break;
        }
    }






    public static void remove_from_bank(String bankName, Player player) {
        File bankFile = getFile(bankName);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);

        List<String> memberList = config.getStringList("members");
        String playerName = player.getName();

        if (!memberList.contains(playerName)) {
            player.sendMessage(ChatColor.RED + "Tu n'es pas membre de cette banque !");
            return;
        }

        memberList.remove(playerName);
        config.set("members", memberList);

        try {
            config.save(bankFile);
            player.sendMessage("Tu as été retiré de la banque " + bankName + " !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void add_perms(String bankName, Player player, String perms) {
        if (player.getUniqueId() == null || perms == null) {
            player.sendMessage("Impossible d'effectuer la commande" + HELPER_WORDS);
        }
        File bankFile = getFile(bankName);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);
        String path = "trust." + player.getUniqueId();

        // Si le path n'existe pas, on le crée
        if (!config.isSet(path)) {
            config.createSection(path);
        }

        List<String> memberList = config.getStringList(path);

        if (memberList.contains(perms)) {
            player.sendMessage("Tu as déjà cette permission !");
            return;
        }

        memberList.add(perms);
        config.set(path, memberList);

        try {
            config.save(bankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setOwner(Player oldOwnerName, Player newOwnerName, String bankName) {
        File bankFile = getFile(bankName);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);

        if (!isOwner(bankName,oldOwnerName)) {
            oldOwnerName.sendMessage("Vous n'êtes pas propriétaire de cette banque");
        }
            if (Bukkit.getOfflinePlayer(newOwnerName.getUniqueId()) == null) {
                oldOwnerName.sendMessage(ChatColor.RED + "Joueur inconnu !");
                return;
            }
            config.set("owner", newOwnerName.getUniqueId().toString());
            oldOwnerName.sendMessage("Tu viens de quitter la banque " + bankName);
            Bank.log(oldOwnerName.getName(), oldOwnerName + " vient de quitter la banque " + bankName);

        try {
            config.save(bankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isOwner(String bankName, Player p) {
        File file = getFile(bankName);
        UUID playerUUID = p.getUniqueId();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (playerUUID.toString().equalsIgnoreCase(config.getString("trust.owner"))){
            return true;
        }
        return false;
    }

    public static Player getPlayer(String playerName) {
        // Vérifier si le joueur est déjà connecté
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            return player;
        }

        // Vérifier si le joueur existe dans la base de données des joueurs
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (!offlinePlayer.hasPlayedBefore() || offlinePlayer.getUniqueId() == null) {
            return null;
        }

        // Récupérer l'objet Player correspondant à l'UUID du joueur
        player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
        if (player == null) {
            return null;
        }

        return player;
    }

    public static List<String> getBank(Player p) {
        UUID playerUUID = p.getUniqueId();
        List<String> banks = new ArrayList<>();
        File folder = DATA_FOLDER;

        if (!folder.exists()) {
            return banks;
        }

        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().endsWith(".yml")) {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    if (file.e) {
                        String bankName = file.getName().replace(".yml", "");
                        banks.add(bankName);
                    }
                }
            }
        }

        return banks;
    }

    public static void taxe() {
        // Récupérer la liste de tous les fichiers de banques existants
        File banksFolder = DATA_FOLDER;
        File[] banksFiles = banksFolder.listFiles();

        // Parcourir tous les fichiers de banques
        for (File bankFile : banksFiles) {
            if (bankFile.isFile()) {
                // Charger la configuration de la banque
                YamlConfiguration config = YamlConfiguration.loadConfiguration(bankFile);

                // Vérifier si la banque a une variable "money" et la décrémenter de 10 si c'est le cas
                if (config.contains("money")) {
                    int currentMoney = config.getInt("money");
                    if (currentMoney >= 10) {
                        config.set("money", currentMoney - 10);
                        try {
                            config.save(bankFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static void clearItemsInRadius(Location center, double radius) {
        World world = center.getWorld();

        // Parcourir tous les objets au sol dans le monde
        for (Item item : world.getEntitiesByClass(Item.class)) {
            Location itemLocation = item.getLocation();

            // Vérifier si l'objet au sol est dans le rayon donné
            if (center.distance(itemLocation) <= radius) {
                // Supprimer l'objet au sol
                item.remove();
            }
        }
    }

    public static void addPermission(UUID uuid,String bank,String permission) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(uuid);
        // Add the permission
        user.data().add(Node.builder(bank+"."+permission).build());

        // Now we need to save changes.
        luckPerms.getUserManager().saveUser(user);
    }

    public static void removePermission(UUID uuid,String bank,String permission) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(uuid);
        // Add the permission
        user.data().remove(Node.builder(bank+"."+permission).build());

        // Now we need to save changes.
        luckPerms.getUserManager().saveUser(user);
    }

    public static void log(String playerName, String action) {
        File file = new File(DATA_FOLDER + File.separator + "logs.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        String dateString = formatter.format(date);

        List<String> logs = config.getStringList("logs");
        logs.add(dateString + " - " + playerName + " - " + action);

        config.set("logs", logs);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<String> getPlayerPermissions(UUID playerUUID) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(playerUUID);

        if (user == null) {
        return new ArrayList<>(); // Retourne une liste vide si le joueur n'est pas trouvé
        }

        QueryOptions queryOptions = luckPerms.getContextManager().getQueryOptions(user).orElse(luckPerms.getContextManager().getStaticQueryOptions());
        List<String> permissions = new ArrayList<>();

        for (Node node : user.resolveInheritedNodes(queryOptions)) {
        if (node.getValue()) {
        permissions.add(node.getKey());
        }
        }

        return permissions;
        }
}
