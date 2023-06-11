package fr.chilli.test;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class SharedAccount implements Economy {

    /**
     * The maximal amount of members a shared account can handle
     */
    public static final int MAX_MEMBERS = 10;
    private final List<OfflinePlayer> members;
    private final UUID uuid;
    private final SharedAccountManager sharedAccountManager;
    private double balance;

    SharedAccount(UUID uuid, OfflinePlayer player, SharedAccountManager sharedAccountManager) {
        this(uuid, new ArrayList<>(List.of(player)), sharedAccountManager);
    }

    SharedAccount(UUID uuid, List<OfflinePlayer> members, SharedAccountManager sharedAccountManager) {
        this.uuid = uuid;
        this.members = members;
        this.sharedAccountManager = sharedAccountManager;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return uuid.toString();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        return sharedAccountManager.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return sharedAccountManager.currencyNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return sharedAccountManager.currencyNameSingular();
    }

    @Override
    public boolean hasAccount(String playerName) {
        for (OfflinePlayer member : this.members) {
            if (Objects.equals(member.getName(), playerName)) return true;
        }
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        for (OfflinePlayer member : this.members) {
            if (Objects.equals(member, player)) return true;
        }
        return false;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return this.hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return this.hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        if (!hasAccount(playerName))
            return 0;
        return balance;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        if (!hasAccount(player))
            return 0;
        return balance;
    }

    @Override
    public double getBalance(String playerName, String world) {
        return this.getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return this.getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        if (!hasAccount(playerName))
            return false;
        return balance >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        if (!hasAccount(player))
            return false;
        return balance >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return this.has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return this.has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (!this.has(playerName, amount))
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, null);

        return this.withdraw(amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (!this.has(player, amount))
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, null);

        return this.withdraw(amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return this.withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return this.withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (!this.has(playerName, amount))
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, null);

        return deposit(amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (!this.has(player, amount))
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, null);

        return deposit(amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return this.depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return this.depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        if (members.size() >= MAX_MEMBERS) return false;
        members.add(Bukkit.getOfflinePlayer(playerName));
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        if (members.size() >= MAX_MEMBERS) return false;
        members.add(player);
        return true;

    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return this.createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return this.createPlayerAccount(player);
    }

    public boolean removePlayerAccount(String playerName) {
        boolean remove = this.members.remove(Bukkit.getOfflinePlayer(playerName));

        if (remove && this.members.size() == 0) {

        }

        return remove;
    }

    public boolean removePlayerAccount(OfflinePlayer player) {
        return this.members.remove(player);

    }

    public boolean removePlayerAccount(String playerName, String worldName) {
        return this.removePlayerAccount(playerName);
    }

    public boolean removePlayerAccount(OfflinePlayer player, String worldName) {
        return this.removePlayerAccount(player);
    }

    public double getBalance() {
        return balance;
    }

    public EconomyResponse deposit(double amount){
        this.balance += amount;
        return new EconomyResponse(amount, this.balance, EconomyResponse.ResponseType.SUCCESS, null);

    }

    public EconomyResponse withdraw(double amount){
        this.balance -= amount;
        return new EconomyResponse(amount, this.balance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    public List<OfflinePlayer> getMembers() {
        return members;
    }

    public void save() {
        Object dbObject = this.sharedAccountManager.getDbObject();
        //todo save to db
    }

}
