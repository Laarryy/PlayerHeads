package dev.laarryy.playerheads.internal;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {

    private final Set<UUID> allThePlayers = new HashSet<>();

    public PlayerData() {
        reload();

        final SkullMaker headsAPI = new SkullMaker();
    }

    public Set<UUID> getDataView() {
        return Collections.unmodifiableSet(allThePlayers);
    }

    public void add(final UUID uuid) {
        allThePlayers.add(uuid);
        // do stuff
    }

    public void add(final OfflinePlayer player) {
        allThePlayers.add(player.getUniqueId());
        // what else?
    }

    public void reload() {
        unload();
        final OfflinePlayer[] literallyEveryoneButLarry = Bukkit.getOfflinePlayers();
        Arrays.stream(literallyEveryoneButLarry).forEach(this::add);
    }

    public void unload() {
        allThePlayers.clear();
        // MORE
    }
}
