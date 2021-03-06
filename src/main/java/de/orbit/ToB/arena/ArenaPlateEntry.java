package de.orbit.ToB.arena;

import de.orbit.ToB.arena.team.TeamType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class ArenaPlateEntry {

    private Arena arena;
    private Location<World> location;
    private TeamType team;

    public ArenaPlateEntry(Arena arena, Location<World> location, TeamType team) {
        this.arena = arena;
        this.location = location;
        this.team = team;
    }

    /**
     * <p>
     *    The location of the pressure plate.
     * </p>
     * @return
     */
    public Location<World> getLocation() {
        return this.location;
    }

    /**
     * <p>
     *    The sign type, it is null if it doesn't belong to any team e.g. LOBBY signs.
     * </p>
     *
     * @return
     */
    public TeamType getTeam() {
        return this.team;
    }

    /**
     * <p>
     *    Remove this plate from the arena.
     * </p>
     */
    public void remove() {
        this.arena.removePlate(this);
    }

}
