package playerheads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

public class EventListener implements Listener
{
    EventListener(final PlayerHeads plugin) {
        super();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    @EventHandler
    public void onPlayerDeathEvent(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (player.getKiller() != null) {
            HeadMaker.spawnPlayerHead(player.getLocation(), PlayerHeads.headList.get(player.getUniqueId()));
        }
    }
    
    @EventHandler
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        PlayerHeads.headList.put(player.getUniqueId(), HeadMaker.getPlayerHead((OfflinePlayer)player, true));
    }
    
    @EventHandler
    public void onPlayerInteractWithEntityEvent(final PlayerInteractEntityEvent event) {
        final Entity e = event.getRightClicked();
        final UUID euuid = e.getUniqueId();
        if (PlayerHeads.otherList.containsKey(euuid)) {
            final Player player = event.getPlayer();
            final UUID uuid = player.getUniqueId();
            event.setCancelled(true);
            if (PlayerHeads.removeQueue.contains(uuid)) {
                PlayerHeads.removeQueue.remove(uuid);
                PlayerHeads.otherEntityList.remove(euuid);
                PlayerHeads.otherList.remove(euuid);
                e.remove();
                player.sendMessage(ChatColor.RED + "Trader Removed.");
            }
            else if (player.getInventory().first(Material.DIAMOND) == -1) {
                player.openInventory((Inventory)PlayerHeadsMenu.poorInventoryList.get(0));
            }
            else {
                player.openInventory((Inventory)PlayerHeadsMenu.richInventoryList.get(0));
            }
        }
    }
    
    @EventHandler
    public void onPlayerToggleSneakEvent(final PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (PlayerHeads.removeQueue.contains(uuid)) {
            PlayerHeads.removeQueue.remove(uuid);
            player.sendMessage(ChatColor.RED + "Cancelled.");
        }
    }
    
    @EventHandler
    public void onEntityDeathEvent(final EntityDeathEvent event) {
        final UUID uuid = event.getEntity().getUniqueId();
        if (PlayerHeads.otherList.containsKey(uuid)) {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                final Inventory inv = player.getOpenInventory().getTopInventory();
                if (PlayerHeadsMenu.poorInventoryList.containsValue(inv) || PlayerHeadsMenu.richInventoryList.containsValue(inv)) {
                    player.closeInventory();
                }
            }
            PlayerHeadsMenu.poorInventoryList.clear();
            PlayerHeadsMenu.richInventoryList.clear();
            PlayerHeadsCommandExecutor.spawnTrader(PlayerHeads.otherList.get(uuid));
            PlayerHeads.otherEntityList.remove(uuid);
            PlayerHeads.otherList.remove(uuid);
        }
    }
    
    @EventHandler
    public void onClickInventoryEvent(final InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        final Inventory inventory = event.getInventory();
        final int slot = event.getRawSlot();
        for (final int index : PlayerHeadsMenu.poorInventoryList.keySet()) {
            final Inventory inv = PlayerHeadsMenu.poorInventoryList.get(index);
            if (inventory.equals(inv)) {
                event.setCancelled(true);
                final ItemStack item = inv.getItem(slot);
                if (item != null) {
                    final Material m = item.getType();
                    if (m == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
                        player.openInventory((Inventory)PlayerHeadsMenu.poorInventoryList.get(index - 1));
                    }
                    else if (m == Material.LIME_STAINED_GLASS_PANE) {
                        player.openInventory((Inventory)PlayerHeadsMenu.poorInventoryList.get(index + 1));
                    }
                }
                return;
            }
        }
        for (final int index : PlayerHeadsMenu.richInventoryList.keySet()) {
            final Inventory inv = PlayerHeadsMenu.richInventoryList.get(index);
            if (event.getInventory().equals(inv)) {
                event.setCancelled(true);
                if (slot >= 0 && slot <= 53) {
                    final ItemStack item = inv.getItem(slot);
                    if (item != null) {
                        final Material m = item.getType();
                        if (m == Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
                            player.openInventory((Inventory)PlayerHeadsMenu.richInventoryList.get(index - 1));
                        }
                        else if (m == Material.LIME_STAINED_GLASS_PANE) {
                            player.openInventory((Inventory)PlayerHeadsMenu.richInventoryList.get(index + 1));
                        }
                        else if (m == Material.PLAYER_HEAD) {
                            final Inventory playerInventory = (Inventory)player.getInventory();
                            final int diamondSlot = playerInventory.first(Material.DIAMOND);
                            final ItemStack diamonds = playerInventory.getItem(diamondSlot);
                            final int a = diamonds.getAmount() - 1;
                            if (a < 1) {
                                playerInventory.clear(diamondSlot);
                            }
                            else {
                                diamonds.setAmount(a);
                                playerInventory.setItem(diamondSlot, diamonds);
                            }
                            if (playerInventory.first(Material.DIAMOND) == -1) {
                                player.openInventory((Inventory)PlayerHeadsMenu.poorInventoryList.get(index));
                            }
                            final ItemStack i = item.clone();
                            final ItemMeta meta = i.getItemMeta();
                            meta.setLore((List)null);
                            i.setItemMeta(meta);
                            i.setAmount(5);
                            HeadMaker.givePlayerItemStack(player, i);
                        }
                    }
                }
            }
        }
    }
}
