package playerheads;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class PlayerHeads extends JavaPlugin
{
    static ConcurrentHashMap<UUID, ItemStack> headList;
    static ConcurrentHashMap<UUID, Location> otherList;
    static ConcurrentHashMap<UUID, Entity> otherEntityList;
    static CopyOnWriteArrayList<UUID> removeQueue;
    static ConcurrentHashMap<UUID, String> base64Values;
    private static File headDataFile;
    private static File otherDataFile;
    
    static {
        PlayerHeads.headList = new ConcurrentHashMap<UUID, ItemStack>();
        PlayerHeads.otherList = new ConcurrentHashMap<UUID, Location>();
        PlayerHeads.otherEntityList = new ConcurrentHashMap<UUID, Entity>();
        PlayerHeads.removeQueue = new CopyOnWriteArrayList<UUID>();
        PlayerHeads.base64Values = new ConcurrentHashMap<UUID, String>();
    }
    
    public PlayerHeads() {
        super();
    }
    
    public void onEnable() {
        this.createHeadDataFile();
        this.createOtherDataFile();
        this.loadHeadDataFileToMemory();
        this.loadOtherDataFileToMemory();
        PlayerHeadsMenu.initialiseItems();
        new EventListener(this);
        final PluginCommand playerHeadsCommand = this.getCommand("src/main/java/playerheads");
        playerHeadsCommand.setExecutor((CommandExecutor)new PlayerHeadsCommandExecutor());
        playerHeadsCommand.setTabCompleter((TabCompleter)new PlayerHeadsTabCompleter());
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, () -> {
            for (Entity e : PlayerHeads.otherEntityList.values()) {
                for (Entity p : e.getNearbyEntities(5.0, 5.0, 5.0)) {
                    if (p instanceof Player) {
                        final Location location = e.getLocation();
                        e.teleport(location.setDirection(p.getLocation().subtract(location).toVector()));
                    }
                }
            }
        }, 0L, 200L);
    }
    
    public void onDisable() {
        this.loadMemoryToHeadDataFile();
        this.loadMemoryToOtherDataFile();
    }
    
    private void createHeadDataFile() {
        try {
            final File folder = new File("plugins/PlayerHeads");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            PlayerHeads.headDataFile = new File(folder, "HeadData.yml");
            if (!PlayerHeads.headDataFile.exists()) {
                PlayerHeads.headDataFile.createNewFile();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createOtherDataFile() {
        try {
            final File folder = new File("plugins/PlayerHeads");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            PlayerHeads.otherDataFile = new File(folder, "OtherData.yml");
            if (!PlayerHeads.otherDataFile.exists()) {
                PlayerHeads.otherDataFile.createNewFile();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadHeadDataFileToMemory() {
        final YamlConfiguration dataconfig = YamlConfiguration.loadConfiguration(PlayerHeads.headDataFile);
        for (final String s : dataconfig.getKeys(false)) {
            final ItemStack item = dataconfig.getItemStack(s);
            if (item != null) {
                final String base64 = HeadMaker.getBase64FromPlayerHead(item);
                if (base64 == null) {
                    continue;
                }
                PlayerHeads.headList.put(UUID.fromString(s), item);
                PlayerHeads.base64Values.put(UUID.fromString(s), base64);
            }
        }
    }
    
    private void loadOtherDataFileToMemory() {
        final YamlConfiguration dataconfig = YamlConfiguration.loadConfiguration(PlayerHeads.otherDataFile);
        for (final String s : dataconfig.getKeys(false)) {
            final UUID uuid = UUID.fromString(dataconfig.getString(s));
            final Entity e = Bukkit.getEntity(uuid);
            if (e != null) {
                PlayerHeads.otherList.put(uuid, this.stringToLocation(s));
                PlayerHeads.otherEntityList.put(uuid, e);
            }
        }
    }
    
    private void loadMemoryToHeadDataFile() {
        PlayerHeads.headDataFile.delete();
        this.createHeadDataFile();
        final YamlConfiguration dataconfig = YamlConfiguration.loadConfiguration(PlayerHeads.headDataFile);
        for (final UUID uuid : PlayerHeads.headList.keySet()) {
            dataconfig.set(uuid.toString(), (Object)PlayerHeads.headList.get(uuid));
        }
        try {
            dataconfig.save(PlayerHeads.headDataFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadMemoryToOtherDataFile() {
        PlayerHeads.otherDataFile.delete();
        this.createOtherDataFile();
        final YamlConfiguration dataconfig = YamlConfiguration.loadConfiguration(PlayerHeads.otherDataFile);
        for (final UUID uuid : PlayerHeads.otherList.keySet()) {
            dataconfig.set(this.locationToString(PlayerHeads.otherList.get(uuid)), (Object)uuid.toString());
        }
        try {
            dataconfig.save(PlayerHeads.otherDataFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Location stringToLocation(final String str) {
        final String[] s = str.split(" ");
        return new Location(Bukkit.getWorld(s[0]), (double)Double.valueOf(s[1]), (double)Double.valueOf(s[2]), (double)Double.valueOf(s[3]));
    }
    
    private String locationToString(final Location location) {
        return String.valueOf(location.getWorld().getName()) + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
    }
    
    private static /* synthetic */ void lambda$0() {
        for (final Entity e : PlayerHeads.otherEntityList.values()) {
            for (final Entity p : e.getNearbyEntities(5.0, 5.0, 5.0)) {
                if (p instanceof Player) {
                    final Location location = e.getLocation();
                    e.teleport(location.setDirection(p.getLocation().subtract(location).toVector()));
                }
            }
        }
    }
}
