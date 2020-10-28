package dev.laarryy.playerheads.commands;

import cloud.commandframework.Command;
import cloud.commandframework.Description;
import cloud.commandframework.arguments.standard.UUIDArgument;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.bukkit.parsers.WorldArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import cloud.commandframework.types.tuples.Triplet;
import dev.laarryy.playerheads.PlayerHeads;
import dev.laarryy.playerheads.internal.npc.NpcManager;
import io.leangen.geantyref.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CommanderKeen {

    private static Vector vectorFromDoubleTriplet(final Triplet<Double, Double, Double> triplet) {
        return new Vector(triplet.getFirst(), triplet.getSecond(), triplet.getThird());
    }

    private final PlayerHeads plugin;
    private final NpcManager npcManager;

    public CommanderKeen(final PlayerHeads plugin) throws Exception {
        this.plugin = plugin;
        npcManager = plugin.getNpcManager();

        final PaperCommandManager<CommandSender> manager;
        manager = new PaperCommandManager<>(plugin, CommandExecutionCoordinator.SimpleCoordinator.simpleCoordinator(),
                                            Function.identity(), Function.identity());

        if (manager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
            manager.registerBrigadier();
        }

        if (manager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }

        buildCommands(manager);
    }

    @SuppressWarnings("ConstantConditions")
    private void spawnNpcAtCoords(final CommandContext<CommandSender> context) {
        final CommandSender sender = context.getSender();
        final Vector locationArgument = context.getOrDefault("location", defaultLocation(sender));
        final World world = context.getOrDefault("world", defaultWorld(sender));

        final Location location = new Location(world, locationArgument.getX(), locationArgument.getY(), locationArgument.getZ());
        npcManager.spawn(location);
    }

    private void linkVillager(final CommandContext<CommandSender> context) {
        final Player player = (Player) context.getSender();
        final UUID uuid = defaultTargetVillager(player);

        if (uuid == null) {
            // say no
            return;
        }

        final Villager villager = (Villager) Bukkit.getEntity(uuid);
        npcManager.link(villager);
    }

    private void unlinkVillager(final CommandContext<CommandSender> context) {
        final CommandSender sender = context.getSender();
        final UUID uuid = context.getOrDefault("uuid", defaultTargetVillager(sender));

        if (uuid == null) {
            // say no
            return;
        }

        final Entity entity = Bukkit.getEntity(uuid);
        if (entity == null || !npcManager.isHeadsNpc(entity)) {
            // say no but louder
            return;
        }

        npcManager.unlink((Villager) entity);
        // unlinked, print something or smth idk, maybe a clickable message to tp there
    }

    private void openHeadsGui(final CommandContext<CommandSender> context) {
        final Player player = (Player) context.getSender();

        // larry takes care of this
    }

    private List<String> suggestNpcUuids(final CommandContext<CommandSender> context, final String current) {
        return npcManager.getLoadedNpcs().stream()
                         .map(UUID::toString)
                         .filter(s -> s.toLowerCase().startsWith(current.toLowerCase()))
                         .collect(Collectors.toList());
    }

    private Vector defaultLocation(final CommandSender sender) {
        if (sender instanceof Player) {
            return ((Player) sender).getLocation().toVector();
        }
        return null;
    }

    private World defaultWorld(final CommandSender sender) {
        if (sender instanceof Player) {
            return ((Player) sender).getWorld();
        }
        return null;
    }

    private UUID defaultTargetVillager(final CommandSender sender) {
        if (!(sender instanceof Player)) {
            return null;
        }

        final Location eyeLocation = ((Player) sender).getEyeLocation();
        final Vector direction = eyeLocation.getDirection();
        final World world = ((Player) sender).getWorld();

        final RayTraceResult result = world.rayTraceEntities(eyeLocation, direction, 5.0);
        if (result == null) {
            return null;
        }

        final Entity hitEntity = result.getHitEntity();
        if (hitEntity == null || hitEntity.getType() != EntityType.VILLAGER) {
            return null;
        }

        return hitEntity.getUniqueId();
    }

    private void buildCommands(final PaperCommandManager<CommandSender> manager) {
        final Command.Builder<CommandSender> base = manager.commandBuilder("playerheads", "pheads", "ph");

        // /ph
        manager.command(base.senderType(Player.class)
                            .handler(this::openHeadsGui))

               // /ph spawnnpc|npc
               .command(base.senderType(Player.class)
                            .literal("spawnnpc", Description.of("Spawns a Heads NPC"), "npc")
                            .handler(this::spawnNpcAtCoords))

               // /ph spawnnpc|npc <<x> <y> <z>>
               .command(base.senderType(Player.class)
                            .literal("spawnnpc", Description.of("Spawns a Heads NPC"), "npc")
                            .argumentTriplet("location", TypeToken.get(Vector.class),
                                             Triplet.of("x", "y", "z"), Triplet.of(Double.class, Double.class, Double.class),
                                             (sender, triplet) -> vectorFromDoubleTriplet(triplet),
                                             Description.of("Spawns a Heads NPC at the given location"))
                            .handler(this::spawnNpcAtCoords))

               // /ph spawnnpc|npc <<x> <y> <z>> <world>
               .command(base.literal("spawnnpc", Description.of("Spawns a Heads NPC"), "npc")
                            .argumentTriplet("location", TypeToken.get(Vector.class),
                                             Triplet.of("x", "y", "z"), Triplet.of(Double.class, Double.class, Double.class),
                                             (sender, triplet) -> vectorFromDoubleTriplet(triplet),
                                             Description.of("Spawns a Heads NPC at the given location"))
                            .argument(WorldArgument.of("world"), Description.of("The world to spawn the Heads NPC in"))
                            .handler(this::spawnNpcAtCoords))

               // /ph convert|link
               .command(base.senderType(Player.class) // TODO: LARRY MAKE THIS DESCRIPTION LOOK GOOD PLEASE THANKS (and the other ones too while you're at it)
                            .literal("convert", Description.of("Converts the villager looked at into a Heads NPC"), "link")
                            .handler(this::linkVillager))

               // /ph unlink
               .command(base.senderType(Player.class)
                            .literal("unlink", Description.of("LArry fill"))
                            .handler(this::unlinkVillager))

               // /ph unlink <uuid>
               .command(base.literal("unlink", Description.of("LArry fill"))
                            .argument(UUIDArgument.<CommandSender>newBuilder("uuid").withSuggestionsProvider(this::suggestNpcUuids),
                                      Description.of("LARRYYYY"))
                            .handler(this::unlinkVillager));
    }
}
