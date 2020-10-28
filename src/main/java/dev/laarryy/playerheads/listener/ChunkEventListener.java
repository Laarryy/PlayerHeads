package dev.laarryy.playerheads.listener;

import dev.laarryy.playerheads.PlayerHeads;
import dev.laarryy.playerheads.internal.npc.NpcManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Arrays;

public class ChunkEventListener implements Listener {

    private final NpcManager npcManager;

    public ChunkEventListener(final PlayerHeads plugin) {
        npcManager = plugin.getNpcManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(final ChunkLoadEvent event) {
        final Chunk chunk = event.getChunk();
        Arrays.stream(chunk.getEntities())
              .filter(entity -> entity.getType() == EntityType.VILLAGER)
              .map(Villager.class::cast)
              .filter(npcManager::isHeadsNpc)
              .map(Entity::getUniqueId)
              .forEach(npcManager::add);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(final ChunkUnloadEvent event) {
        final Chunk chunk = event.getChunk();
        Arrays.stream(chunk.getEntities())
              .map(Entity::getUniqueId)
              .forEach(npcManager::remove);
    }
}
