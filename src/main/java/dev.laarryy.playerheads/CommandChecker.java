package dev.laarryy.playerheads;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class CommandChecker
{
    CommandChecker() {
        super();
    }
    
    static List<String> tryAddUsageString(final Map<Boolean, String> map, final List<String> usageList) {
        final String str = map.get(false);
        if (str != null) {
            usageList.add(str);
        }
        return usageList;
    }
    
    static void sendResponseToSender(final List<String> usage, final CommandSender sender) {
        if (usage.size() == 0) {
            sender.sendMessage("You do not have permission to use this command.");
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Usage:");
        for (final String str : usage) {
            sb.append("\n");
            sb.append(str);
        }
        sender.sendMessage(sb.toString());
    }
    
    static boolean mapToBool(final Map<Boolean, ?> map) {
        final Optional<Boolean> d1 = map.keySet().stream().findFirst();
        return d1.isPresent() && d1.get();
    }
    
    static Map<Boolean, String> commandChecker(final String[] args, final boolean hasPermission, final int argsRequiredLength, final String[] list, final String usageString) {
        return commandHandler(args, hasPermission, argsRequiredLength, 0, list, usageString, 0);
    }
    
    static Map<Boolean, String> commandChecker(final String[] args, final boolean hasPermission, final int argsRequiredLengthMin, final int argsRequiredLengthMax, final String[] list, final String usageString) {
        int type;
        if (argsRequiredLengthMax == -1) {
            type = 2;
        }
        else {
            type = 1;
        }
        return commandHandler(args, hasPermission, argsRequiredLengthMin, argsRequiredLengthMax, list, usageString, type);
    }
    
    private static Map<Boolean, String> commandHandler(final String[] args, final boolean hasPermission, final int argsRequiredLength1, final int argsRequiredLength2, final String[] list, final String usageString, final int type) {
        if (!hasPermission) {
            return Collections.singletonMap(false, (String)null);
        }
        if (list.length <= args.length) {
            for (int i = 0; i < list.length; ++i) {
                if (!list[i].equalsIgnoreCase(args[i])) {
                    return Collections.singletonMap(false, usageString);
                }
            }
            if (type == 0) {
                if (typeOneCheck(argsRequiredLength1, args.length)) {
                    return Collections.singletonMap(false, usageString);
                }
            }
            else if (type == 2) {
                if (typeThreeCheck(argsRequiredLength1, args.length)) {
                    return Collections.singletonMap(false, usageString);
                }
            }
            else if (typeTwoCheck(argsRequiredLength1, argsRequiredLength2, args.length)) {
                return Collections.singletonMap(false, usageString);
            }
            return Collections.singletonMap(true, (String)null);
        }
        return Collections.singletonMap(false, usageString);
    }
    
    private static boolean typeOneCheck(final int argsRequiredLength, final int argsLength) {
        return argsLength != argsRequiredLength;
    }
    
    private static boolean typeTwoCheck(final int argsRequiredLengthMin, final int argsRequiredLengthMax, final int argsLength) {
        return argsLength > argsRequiredLengthMax || argsLength < argsRequiredLengthMin;
    }
    
    private static boolean typeThreeCheck(final int argsRequiredLengthMin, final int argsLength) {
        return argsLength < argsRequiredLengthMin;
    }
}
