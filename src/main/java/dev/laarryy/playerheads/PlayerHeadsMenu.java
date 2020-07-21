package playerheads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class PlayerHeadsMenu
{
    static ConcurrentHashMap<Integer, Inventory> richInventoryList;
    static ConcurrentHashMap<Integer, Inventory> poorInventoryList;
    
    static {
        PlayerHeadsMenu.richInventoryList = new ConcurrentHashMap<Integer, Inventory>();
        PlayerHeadsMenu.poorInventoryList = new ConcurrentHashMap<Integer, Inventory>();
    }
    
    PlayerHeadsMenu() {
        super();
    }
    
    static void initialiseItems() {
        final int size = PlayerHeads.headList.size();
        int amount = (int)Math.ceil(size / 52.0);
        if (amount == 0) {
            amount = 1;
        }
        final int amountminus = amount - 1;
        final ItemStack[] m = new ItemStack[size];
        int t = 0;
        for (final ItemStack i : PlayerHeads.headList.values()) {
            m[t] = i;
            ++t;
        }
        for (int j = 0; j < amount; ++j) {
            PlayerHeadsMenu.poorInventoryList.put(j, Bukkit.createInventory((InventoryHolder)null, 54, "5 Player Heads For 1 Diamond!"));
            PlayerHeadsMenu.richInventoryList.put(j, Bukkit.createInventory((InventoryHolder)null, 54, "5 Player Heads For 1 Diamond!"));
            if (amount == 1) {
                initialiseItems(j, 3, m);
            }
            else if (j == 0) {
                initialiseItems(j, 0, m);
            }
            else if (j == amountminus) {
                initialiseItems(j, 2, m);
            }
            else {
                initialiseItems(j, 1, m);
            }
        }
    }
    
    private static void initialiseItems(final int index, final int pIndex, final ItemStack[] items) {
        final Inventory inventoryRich = PlayerHeadsMenu.richInventoryList.get(index);
        final Inventory inventoryPoor = PlayerHeadsMenu.poorInventoryList.get(index);
        setPageButtons(pIndex, inventoryRich);
        setHeads(index, inventoryRich, items, true);
        setPageButtons(pIndex, inventoryPoor);
        setHeads(index, inventoryPoor, items, false);
    }
    
    private static void setHeads(final int index, final Inventory inventory, final ItemStack[] items, final boolean isRich) {
        final int min = index * 52;
        final int max = min + 52;
        final int size = items.length;
        for (int t = min; t < max; ++t) {
            if (size > t) {
                final ItemStack i = items[t].clone();
                i.setAmount(1);
                final ItemMeta meta = i.getItemMeta();
                final List<String> s = new ArrayList<String>();
                if (isRich) {
                    s.add(ChatColor.AQUA + "Click here to purchase!");
                }
                else {
                    s.add(ChatColor.RED + "You need more diamonds!");
                }
                meta.setLore((List)s);
                i.setItemMeta(meta);
                inventory.setItem(t - min, i);
            }
        }
    }
    
    private static void setPageButtons(final int pIndex, final Inventory inventory) {
        final ItemStack blackPane = createGuiItem(" ", Material.BLACK_STAINED_GLASS_PANE);
        final ItemStack lightBluePane = createGuiItem(ChatColor.GOLD + "Previous Page", Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        final ItemStack limePane = createGuiItem(ChatColor.GOLD + "Next Page", Material.LIME_STAINED_GLASS_PANE);
        switch (pIndex) {
            case 0: {
                inventory.setItem(53, limePane);
                inventory.setItem(52, blackPane);
                break;
            }
            case 1: {
                inventory.setItem(53, limePane);
                inventory.setItem(52, lightBluePane);
                break;
            }
            case 2: {
                inventory.setItem(53, blackPane);
                inventory.setItem(52, lightBluePane);
                break;
            }
            case 3: {
                inventory.setItem(53, blackPane);
                inventory.setItem(52, blackPane);
                break;
            }
        }
    }
    
    private static ItemStack createGuiItem(final String name, final Material material) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        return item;
    }
}
