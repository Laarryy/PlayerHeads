package dev.laarryy.playerheads;

import dev.laarryy.playerheads.internal.PlayerData;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;

public final class PlayerHeads extends JavaPlugin {

    private final PlayerData playerData = new PlayerData();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onLoad() {
        // load things from storage?
    }

    @Override
    public void onEnable() {
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
}
