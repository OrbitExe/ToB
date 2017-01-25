package de.orbit.ToB;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class Hologram implements Component {

    private static List<Entity> holograms = new ArrayList<>();

    public Hologram() {}

    public boolean spawn(Text text, Location<World> location) {

        Entity armorStand = location.getExtent().createEntity(EntityTypes.ARMOR_STAND, location.getPosition());

        // Spawning
        if(
            !(
                location.getExtent().spawnEntity(
                    armorStand,
                    Cause.source(
                        EntitySpawnCause.builder().entity(armorStand).type(SpawnTypes.PLUGIN).build()
                    ).build()
                )
            )
        ) {
            return false;
        }

        // properties
        armorStand.offer(Keys.DISPLAY_NAME, text);
        armorStand.offer(Keys.HAS_GRAVITY, false);
        armorStand.offer(Keys.CUSTOM_NAME_VISIBLE, true);
        armorStand.offer(Keys.ARMOR_STAND_MARKER, true);
        armorStand.offer(Keys.INVISIBLE, true);

        Hologram.holograms.add(armorStand);

        return true;

    }

    @Override
    public void setup() {
        ToB.getLogger().info("Hologram - Successfully completed the setup.");
    }

    @Override
    public void shutdown() {
        int n = Hologram.holograms.size();
        Hologram.holograms.forEach(Entity::remove);
        ToB.getLogger().info(String.format("Hologram - Shutdown %d registered holograms.", n));
    }

}
