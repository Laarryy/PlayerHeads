package dev.laarryy.playerheads;


import dev.laarryy.playerheads.internal.PlayerData;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public final class PlayerHeads extends JavaPlugin {
    PlayerData playerData = new PlayerData();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onLoad() {
        // load things from storage?
    }

    @Override
    public void onEnable() {
        ArrayList<OfflinePlayer> playerArray = playerData.getData();
        playerArray.forEach(offlinePlayer -> logger.info("Player's UUID: " + offlinePlayer.getUniqueId().toString()));
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
}
