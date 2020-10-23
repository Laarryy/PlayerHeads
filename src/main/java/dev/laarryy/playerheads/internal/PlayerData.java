package dev.laarryy.playerheads.internal;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class PlayerData {

    ArrayList<OfflinePlayer> playerArrayList = new ArrayList<>();

    public PlayerData() {
        SkullMaker headsAPI = new SkullMaker();
    }

    public ArrayList<OfflinePlayer> getData() {
        OfflinePlayer[] playerList = Bukkit.getOfflinePlayers();
        Iterator<OfflinePlayer> offlinePlayerIterator = Arrays.stream(playerList).iterator();
        offlinePlayerIterator.forEachRemaining(this::addToArray);
        return playerArrayList;
    }

    private void addToArray(OfflinePlayer offlinePlayer) {
        playerArrayList.add(offlinePlayer);

    }

}
