package dev.laarryy.playerheads.internal.npc;

import dev.laarryy.playerheads.PlayerHeads;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public final class NpcManager {

    private static final Byte DUMMY = (byte) 0x01;
    private static final AttributeModifier ATTRIBUTE_MODIFIER = new AttributeModifier("heads-npc", 0.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

    private final PlayerHeads plugin;
    private final Set<UUID> loadedNpcs = new LinkedHashSet<>();

    public NpcManager(final PlayerHeads plugin) {
        this.plugin = plugin;
    }

    public boolean isHeadsNpc(final Entity entity) {
        return entity.getPersistentDataContainer().has(plugin.identifierKey, PersistentDataType.BYTE);
    }

    public Set<UUID> getLoadedNpcs() {
        return Collections.unmodifiableSet(loadedNpcs);
    }

    public void add(final UUID uuid) {
        final Entity entity = Bukkit.getEntity(uuid);
        if (entity == null || !isHeadsNpc(entity)) {
            return;
        }

        loadedNpcs.add(uuid);
    }

    public void remove(final UUID uuid) {
        loadedNpcs.remove(uuid);
    }

    @SuppressWarnings("ConstantConditions")
    public Villager spawn(final Location location) {
        final World world = location.getWorld();

        return world.spawn(location, Villager.class, villager -> {
            villager.setProfession(Villager.Profession.CLERIC);
            villager.setVillagerType(Villager.Type.TAIGA);
            villager.setCustomNameVisible(true);
            villager.setCustomName("§3Not Larry");
            link(villager);
        });
    }

    public void kill(final Villager npc) {
        if (!isHeadsNpc(npc)) {
            return;
        }

        loadedNpcs.remove(npc.getUniqueId());
        npc.remove();
    }

    @SuppressWarnings("ConstantConditions")
    public void link(final Villager npc) {
        final PersistentDataContainer pdc = npc.getPersistentDataContainer();
        pdc.set(plugin.identifierKey, PersistentDataType.BYTE, DUMMY);
        loadedNpcs.add(npc.getUniqueId());

        npc.setPersistent(true);
        npc.setInvulnerable(true);
        npc.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(ATTRIBUTE_MODIFIER);
    }

    @SuppressWarnings("ConstantConditions")
    public boolean unlink(final Villager npc) {
        if (!isHeadsNpc(npc)) {
            return false;
        }

        final PersistentDataContainer pdc = npc.getPersistentDataContainer();
        pdc.remove(plugin.identifierKey);
        loadedNpcs.remove(npc.getUniqueId());

        npc.setPersistent(false);
        npc.setInvulnerable(false);
        npc.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(ATTRIBUTE_MODIFIER);
        return true;
    }
}
