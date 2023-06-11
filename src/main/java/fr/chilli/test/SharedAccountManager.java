package fr.chilli.test;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

public final class SharedAccountManager extends AbstractEconomy {

    private final Map<UUID, SharedAccount> sharedAccounts;
    private final Map<OfflinePlayer, Double> playerMoney;
    private final Object dbObject;

    public SharedAccountManager(Object dbObject) {
        this.dbObject = dbObject;
        this.playerMoney = this.loadPlayerMoney();
        this.sharedAccounts = this.loadSharedAccounts();
    }

    public SharedAccount createSharedAccount(OfflinePlayer player) {
        //generate a new random uuid
        UUID uuid = UUID.randomUUID();
        while (!this.sharedAccounts.containsKey(uuid)) uuid = UUID.randomUUID();

        return this.createSharedAccount(player, uuid);
    }

    public SharedAccount createSharedAccount(OfflinePlayer player, UUID uuid) {
        SharedAccount sharedAccount = new SharedAccount(uuid, player, this);
        this.sharedAccounts.put(uuid, sharedAccount);

        return sharedAccount;
    }


    public void deleteSharedAccount(SharedAccount toDelete) {
        //todo remove shared account from db
    }

    public List<SharedAccount> getSharedAccountsOfPlayer(OfflinePlayer player) {
        List<SharedAccount> sharedAccounts1 = new ArrayList<>();

        for (SharedAccount value : this.sharedAccounts.values())
            if (value.getMembers().contains(player))
                sharedAccounts1.add(value);

        return sharedAccounts1;
    }

    public void saveAll() {
        this.sharedAccounts.forEach((uuid, sharedAccount) -> sharedAccount.save());
    }

    private Map<UUID, SharedAccount> loadSharedAccounts() {
        return new HashMap<>();//todo load db entries
    }

    private Map<OfflinePlayer, Double> loadPlayerMoney() {
        return new HashMap<>();//todo load db entries
    }

    public Object getDbObject() {
        return dbObject;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "SharedAccounts";
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        return String.valueOf((double) ((int) (amount * Math.pow(10, fractionalDigits()))) / Math.pow(10, fractionalDigits()));
    }

    @Override
    public String currencyNamePlural() {
        return "";//todo
    }

    @Override
    public String currencyNameSingular() {
        return "";//todo
    }

    @Override
    public boolean hasAccount(String playerName) {
        for (SharedAccount value : this.sharedAccounts.values()) {
            if (value.hasAccount(playerName)) return true;
        }
        return false;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        for (SharedAccount value : this.sharedAccounts.values()) {
            if (value.hasAccount(playerName)) return true;
        }
        return false;
    }

    @Override
    public double getBalance(String playerName) {
        return this.playerMoney.get(Bukkit.getOfflinePlayer(playerName));
    }

    @Override
    public double getBalance(String playerName, String world) {
        return this.getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        if (!hasAccount(playerName))
            return false;
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return this.has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (!this.has(playerName, amount))
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, null);

        this.playerMoney.put(Bukkit.getOfflinePlayer(playerName), this.playerMoney.get(Bukkit.getOfflinePlayer(playerName)) - amount);
        return new EconomyResponse(amount, this.playerMoney.get(Bukkit.getOfflinePlayer(playerName)), EconomyResponse.ResponseType.SUCCESS, null);

    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return this.withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (!this.has(playerName, amount))
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, null);

        this.playerMoney.put(Bukkit.getOfflinePlayer(playerName), this.playerMoney.get(Bukkit.getOfflinePlayer(playerName)) + amount);
        return new EconomyResponse(amount, this.playerMoney.get(Bukkit.getOfflinePlayer(playerName)), EconomyResponse.ResponseType.SUCCESS, null);

    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return this.depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        try{
            createSharedAccount(Bukkit.getOfflinePlayer(player), UUID.fromString(name));
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
        }catch(IllegalArgumentException e){
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        try{
            deleteSharedAccount(sharedAccounts.get(UUID.fromString(name)));
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
        }catch(IllegalArgumentException e){
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        try{
            SharedAccount sharedAccount = sharedAccounts.get(UUID.fromString(name));
            return new EconomyResponse(0, sharedAccount.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
        }catch(IllegalArgumentException e){
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        try{
            SharedAccount sharedAccount = sharedAccounts.get(UUID.fromString(name));
            return new EconomyResponse(0, 0, sharedAccount.getBalance() >= amount
                    ? EconomyResponse.ResponseType.SUCCESS
                    : EconomyResponse.ResponseType.FAILURE,
                                       null);
        }catch(IllegalArgumentException e){
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        try{
            if (!bankHas(name, amount).transactionSuccess())
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, null);

            return sharedAccounts.get(UUID.fromString(name)).withdraw(amount);
        }catch(IllegalArgumentException e){
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        try{
            if (!bankHas(name, amount).transactionSuccess())
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, null);

            return sharedAccounts.get(UUID.fromString(name)).deposit(amount);
        }catch(IllegalArgumentException e){
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, e.getMessage());
        }
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, null);
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        try{
            return sharedAccounts.get(UUID.fromString(name)).getMembers().contains(Bukkit.getOfflinePlayer(playerName)) ?
                    new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null)
                    : new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, null);

        }catch(IllegalArgumentException e){
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, e.getMessage());
        }
    }

    @Override
    public List<String> getBanks() {
        List<String> strings = new ArrayList<>();

        sharedAccounts.forEach((uuid, sharedAccount) -> strings.add(uuid.toString()));

        return strings;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (this.playerMoney.containsKey(offlinePlayer)) return false;
        this.playerMoney.put(offlinePlayer, (double) 0);
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return this.createPlayerAccount(Bukkit.getOfflinePlayer(playerName));
    }

}
