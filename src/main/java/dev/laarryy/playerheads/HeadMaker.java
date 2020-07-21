package playerheads;

import net.minecraft.server.v1_16_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.UUID;

class HeadMaker
{
    HeadMaker() {
        super();
    }
    
    static ItemStack getPlayerHead(final OfflinePlayer player, final boolean storeBase64ToArray) {
        final ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta skullMeta = (SkullMeta)item.getItemMeta();
        skullMeta.setOwningPlayer(player);
        item.setItemMeta(skullMeta);
        final String base64 = getBase64FromPlayerHead(item);
        if (base64 == null) {
            return new ItemStack(Material.PLAYER_HEAD);
        }
        if (storeBase64ToArray) {
            PlayerHeads.base64Values.put(player.getUniqueId(), base64);
        }
        return getNamedPlayerHeadFromBase64(base64, ChatColor.GOLD + player.getName() + "'s Head");
    }
    
    static ItemStack getNamedPlayerHeadFromBase64(final String base64, final String displayName) {
        final ItemStack item = getPlayerHeadFromBase64(base64);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }
    
    private static ItemStack getPlayerHeadFromBase64(final String base64) {
        return Bukkit.getUnsafe().modifyItemStack(new ItemStack(Material.PLAYER_HEAD), "{SkullOwner:{Id:\"" + new UUID(base64.hashCode(), base64.hashCode()) + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}");
    }
    
    static String getBase64FromPlayerHead(final ItemStack item) {
        final net.minecraft.server.v1_16_R1.NBTTagCompound c = CraftItemStack.asNMSCopy(item).getTag();
        String value = null;
        if (c != null) {
            final NBTTagCompound skullOwner = c.getCompound("SkullOwner");
            if (skullOwner != null) {
                final String s = skullOwner.getCompound("Properties").toString();
                final int i = s.indexOf("Value:\"") + 7;
                if (i != 6) {
                    value = s.substring(i).split("\"")[0];
                }
            }
        }
        return value;
    }
    
    static void spawnPlayerHead(final Location location, final ItemStack head) {
        location.getWorld().dropItemNaturally(location, head);
    }
    
    static void givePlayerItemStack(final Player player, final ItemStack itemStack) {
        final HashMap<Integer, ItemStack> m = (HashMap<Integer, ItemStack>)player.getInventory().addItem(new ItemStack[] { itemStack });
        for (final ItemStack item : m.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }
}
