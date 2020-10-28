package dev.laarryy.playerheads;

import dev.laarryy.playerheads.commands.CommanderKeen;
import dev.laarryy.playerheads.internal.npc.NpcManager;
import dev.laarryy.playerheads.internal.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

public final class PlayerHeads extends JavaPlugin {

    private final PlayerData playerData = new PlayerData();
    private final NpcManager npcManager = new NpcManager(this);
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Executor syncExecutor = command -> Bukkit.getScheduler().runTask(this, command);
    private final Executor asyncExecutor = command -> Bukkit.getScheduler().runTaskAsynchronously(this, command);

    public final NamespacedKey identifierKey = new NamespacedKey(this, "heads-npc");

    @Override
    public void onLoad() {
        // load things from storage?
    }

    @Override
    public void onEnable() {
        try {
            new CommanderKeen(this);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final Set<UUID> playerArray = playerData.getDataView();
        playerArray.forEach(uuid -> logger.info("Player's UUID: " + uuid));
    }

    @Override
    public void onDisable() {
        // save and unload
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        // probably some custom handling to propagate reloads throughout other config-dependent objects
    }

    // renamed because return type isn't the same as superclass's getLogger
    public Logger getPluginLogger() {
        return logger;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public NpcManager getNpcManager() {
        return npcManager;
    }

    public Executor getSyncExecutor() {
        return syncExecutor;
    }

    public Executor getAsyncExecutor() {
        return asyncExecutor;
    }
}
