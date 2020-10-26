package dev.laarryy.playerheads.listener;

import dev.laarryy.playerheads.PlayerHeads;
import dev.laarryy.playerheads.internal.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {

    final PlayerHeads plugin;
    final PlayerData playerData;

    public PlayerJoinListener(final PlayerHeads plugin) {
        this.plugin = plugin;
        this.playerData = plugin.getPlayerData();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        playerData.add(event.getPlayer());
    }
}
