package dev.laarryy.playerheads;

import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerHeads extends JavaPlugin {

    @Override
    public void onLoad() {
        // load things from storage?
    }

    @Override
    public void onEnable() {
        // register things
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
