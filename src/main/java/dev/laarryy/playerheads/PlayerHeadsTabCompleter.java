package playerheads;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerHeadsTabCompleter implements TabCompleter
{
    public PlayerHeadsTabCompleter() {
        super();
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        final boolean isPlayer = sender instanceof Player;
        if (isPlayer) {
            final boolean hasMasterPermission = sender.hasPermission("vendettacraft.playerheads.*");
            final ArrayList<String> listf = new ArrayList<String>();
            if (args.length == 1) {
                final String a = args[0].toLowerCase();
                if ((hasMasterPermission || sender.hasPermission("vendettacraft.playerheads.give")) && "give".startsWith(a)) {
                    listf.add("give");
                }
                if ((hasMasterPermission || sender.hasPermission("vendettacraft.playerheads.spawntrader")) && "spawntrader".startsWith(a)) {
                    listf.add("spawntrader");
                }
                if ((hasMasterPermission || sender.hasPermission("vendettacraft.playerheads.removetrader")) && "removetrader".startsWith(a)) {
                    listf.add("removetrader");
                }
                if ((hasMasterPermission || sender.hasPermission("vendettacraft.playerheads.get")) && "get".startsWith(a)) {
                    listf.add("get");
                }
                if (listf.size() == 0) {
                    return null;
                }
                return listf;
            }
            else if (args.length == 2) {
                if ((hasMasterPermission || sender.hasPermission("vendettacraft.playerheads.spawntrader")) && args[0].equalsIgnoreCase("spawntrader")) {
                    listf.add("~");
                    listf.add("~ ~");
                    listf.add("~ ~ ~");
                }
                if ((hasMasterPermission || sender.hasPermission("vendettacraft.playerheads.get")) && args[0].equalsIgnoreCase("get")) {
                    listf.add("<Base64>");
                }
                if (listf.size() == 0) {
                    return null;
                }
                return listf;
            }
            else if (args.length == 3) {
                if ((hasMasterPermission || sender.hasPermission("vendettacraft.playerheads.spawntrader")) && args[0].equalsIgnoreCase("spawntrader")) {
                    listf.add("~");
                    listf.add("~ ~");
                }
                if ((hasMasterPermission || sender.hasPermission("vendettacraft.playerheads.give")) && args[0].equalsIgnoreCase("give")) {
                    listf.add("<Amount>");
                }
                if ((hasMasterPermission || sender.hasPermission("vendettacraft.playerheads.get")) && args[0].equalsIgnoreCase("get")) {
                    listf.add("<Amount>");
                }
                if (listf.size() == 0) {
                    return null;
                }
                return listf;
            }
            else if (args.length >= 4) {
                if (args.length == 4 && (hasMasterPermission || sender.hasPermission("vendettacraft.playerheads.spawntrader")) && args[0].equalsIgnoreCase("spawntrader")) {
                    listf.add("~");
                }
                if ((hasMasterPermission || sender.hasPermission("vendettacraft.playerheads.get")) && args[0].equalsIgnoreCase("get")) {
                    listf.add("<DisplayName>");
                }
                if (listf.size() == 0) {
                    return null;
                }
                return listf;
            }
        }
        return null;
    }
}
