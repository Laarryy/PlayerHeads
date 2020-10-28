package dev.laarryy.playerheads.listener;

import dev.laarryy.playerheads.PlayerHeads;
import dev.laarryy.playerheads.internal.npc.NpcManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    private final NpcManager npcManager;

    public EntityDeathListener(final PlayerHeads playerHeads) {
        npcManager = playerHeads.getNpcManager();
        Bukkit.getPluginManager().registerEvents(this, playerHeads);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(final EntityDeathEvent event) {
        npcManager.remove(event.getEntity().getUniqueId());
    }
}
