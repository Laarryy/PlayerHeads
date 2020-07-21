package playerheads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerHeadsCommandExecutor implements CommandExecutor
{
    public PlayerHeadsCommandExecutor() {
        super();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final boolean hasMasterPerm = this.hasMasterPermission(sender);
        final boolean isPlayer = sender instanceof Player;
        boolean lock = false;
        List<String> usage = new ArrayList<String>();
        boolean doResponse = true;
        Map<Boolean, String> map = CommandChecker.commandChecker(args, isPlayer && (hasMasterPerm || sender.hasPermission("vendettacraft.playerheads.give")), 2, 3, new String[] { "give" }, "/playerheads give [PlayerName] {Amount}");
        if (CommandChecker.mapToBool(map)) {
            this.giveItemCommand((Player)sender, args);
            doResponse = false;
        }
        else {
            if (args.length > 0 && args[0].equalsIgnoreCase("give")) {
                lock = true;
            }
            usage = CommandChecker.tryAddUsageString(map, usage);
        }
        map = CommandChecker.commandChecker(args, isPlayer && (hasMasterPerm || sender.hasPermission("vendettacraft.playerheads.removetrader")), 1, new String[] { "removetrader" }, "/playerheads removetrader");
        if (CommandChecker.mapToBool(map)) {
            this.removeTraderCommand((Player)sender);
            doResponse = false;
        }
        else {
            boolean t = false;
            if (args.length > 0 && args[0].equalsIgnoreCase("removetrader")) {
                usage.clear();
                t = true;
            }
            if (!lock) {
                usage = CommandChecker.tryAddUsageString(map, usage);
            }
            if (t) {
                lock = true;
            }
        }
        map = CommandChecker.commandChecker(args, isPlayer && (hasMasterPerm || sender.hasPermission("vendettacraft.playerheads.get")), 2, -1, new String[] { "get" }, "/playerheads get [Base64] {Amount} {DisplayName}");
        if (CommandChecker.mapToBool(map)) {
            this.getItemCommand((Player)sender, args);
            doResponse = false;
        }
        else {
            boolean t = false;
            if (args.length > 0 && args[0].equalsIgnoreCase("get")) {
                usage.clear();
                t = true;
            }
            if (!lock) {
                usage = CommandChecker.tryAddUsageString(map, usage);
            }
            if (t) {
                lock = true;
            }
        }
        map = CommandChecker.commandChecker(args, isPlayer && (hasMasterPerm || sender.hasPermission("vendettacraft.playerheads.spawntrader")), 4, new String[] { "spawntrader" }, "/playerheads spawntrader [x] [y] [z]");
        if (CommandChecker.mapToBool(map)) {
            this.spawnTraderCommand((Player)sender, args);
            doResponse = false;
        }
        else {
            if (args.length > 0 && args[0].equalsIgnoreCase("spawntrader")) {
                usage.clear();
            }
            if (!lock) {
                usage = CommandChecker.tryAddUsageString(map, usage);
            }
        }
        if (doResponse) {
            CommandChecker.sendResponseToSender(usage, sender);
        }
        return true;
    }
    
    private void removeTraderCommand(final Player player) {
        PlayerHeads.removeQueue.add(player.getUniqueId());
        player.sendMessage(ChatColor.RED + "Right click on the trader you wish to remove. (Sneak To Cancel)");
    }
    
    private void spawnTraderCommand(final Player player, final String[] args) {
        final Location location = this.parseStringToLocation(player, new String[] { args[1], args[2], args[3] });
        if (location != null) {
            spawnTrader(location);
        }
    }
    
    static void spawnTrader(final Location location) {
        PlayerHeadsMenu.initialiseItems();
        final Villager villager = (Villager)location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        final UUID uuid = villager.getUniqueId();
        PlayerHeads.otherList.put(uuid, location);
        PlayerHeads.otherEntityList.put(uuid, (Entity)villager);
        villager.setProfession(Villager.Profession.LIBRARIAN);
        villager.setAI(false);
        villager.setCustomName(ChatColor.GOLD + "Player Head Trader");
        villager.setSilent(true);
        villager.setLootTable(LootTables.EMPTY.getLootTable());
        villager.setInvulnerable(true);
        villager.setCollidable(false);
    }
    
    private void getItemCommand(final Player player, final String[] args) {
        String displayName;
        if (args.length >= 4) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 3; i < args.length; ++i) {
                sb.append(args[i]);
                sb.append(" ");
            }
            displayName = ChatColor.GOLD + sb.toString();
        }
        else {
            displayName = "Player Head";
        }
        for (final String v : PlayerHeads.base64Values.values()) {
            if (v.equalsIgnoreCase(args[1])) {
                player.sendMessage(ChatColor.RED + "This item is only available to purchase from a player head trader.");
                return;
            }
        }
        ItemStack item = HeadMaker.getNamedPlayerHeadFromBase64(args[1], displayName);
        if (args.length >= 3) {
            item = this.setAmountWithString(args[2], item);
        }
        HeadMaker.givePlayerItemStack(player, item);
    }
    
    private void giveItemCommand(final Player player, final String[] args) {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
        final UUID uuid = offlinePlayer.getUniqueId();
        ItemStack item;
        if (PlayerHeads.headList.containsKey(uuid)) {
            item = PlayerHeads.headList.get(uuid);
        }
        else {
            item = HeadMaker.getPlayerHead(offlinePlayer, false);
        }
        if (args.length == 3) {
            item = this.setAmountWithString(args[2], item);
        }
        HeadMaker.givePlayerItemStack(player, item);
    }
    
    private ItemStack setAmountWithString(final String amount, final ItemStack item) {
        int i = 1;
        if (amount.matches("^[0-9]+$")) {
            i = Integer.parseInt(amount);
        }
        if (i == 0) {
            item.setAmount(1);
        }
        else if (i > 64) {
            item.setAmount(64);
        }
        else {
            item.setAmount(i);
        }
        return item;
    }
    
    private boolean hasMasterPermission(final CommandSender sender) {
        return sender.hasPermission("vendettacraft.playerheads.*");
    }
    
    private Location parseStringToLocation(final Player player, final String[] args) {
        final int[] xyz = new int[3];
        for (int n = 0; n < args.length; ++n) {
            final Object i = this.parseStringToInteger(args[n], player, n);
            if (i == null) {
                return null;
            }
            xyz[n] = (int)i;
        }
        return new Location(player.getWorld(), (double)xyz[0], (double)xyz[1], (double)xyz[2]);
    }
    
    private Object parseStringToInteger(String str, final Player player, final int type) {
        boolean isNegative = false;
        boolean isRelative = false;
        final boolean isZeroRelative = str.equals("~");
        if (str.startsWith("~")) {
            isRelative = true;
            str = str.replaceFirst("~", "");
        }
        if (str.startsWith("-")) {
            isNegative = true;
            str = str.replaceFirst("-", "");
        }
        final boolean isMatching = str.matches("^[0-9]+$");
        if (isMatching || isZeroRelative) {
            int i = 0;
            if (isMatching) {
                i = Integer.parseInt(str);
            }
            if (isNegative) {
                i *= -1;
            }
            if (isRelative) {
                switch (type) {
                    case 0: {
                        i += player.getLocation().getBlockX();
                        break;
                    }
                    case 1: {
                        i += player.getLocation().getBlockY();
                        break;
                    }
                    case 2: {
                        i += player.getLocation().getBlockZ();
                        break;
                    }
                }
            }
            return i;
        }
        return null;
    }
}
