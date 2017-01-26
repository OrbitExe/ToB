package de.orbit.ToB.arena;

import de.orbit.ToB.Component;
import de.orbit.ToB.ToB;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ArenaManager implements Component {

    private Map<Integer, Arena> arenas = new HashMap<>();

    public ArenaManager() {}

    /**
     * <p>
     *    Returns a corresponding ArenaPlayer if one exists, if no one exist
     * </p>
     *
     * @param player
     * @return
     */
    public Optional<ArenaPlayer> getPlayer(Player player) {
        Optional<Arena> arena = this.arenas.values().stream().filter(e -> e.hasPlayer(player)).findAny();

        if(arena.isPresent()) {
            return arena.get().getPlayer(player);
        }

        return Optional.empty();
    }

    /**
     * <p>
     *    Adds an arena to the internal storage and loads it if required.
     * </p>
     *
     * @param arena The arena object.
     * @param load If the data should be loaded from a database or not.
     * @return True, if it was successful, otherwise false.
     */
    public Optional<String> add(Arena arena, boolean load) {

        //--- If the max player amount is high enough
        //if(arena.getMaxPlayers() < 2) {
        //    return Optional.of("An arena must at least hold 2 players.");
        //}

        //--- If the amount of max-players is even.
        //if(!(arena.getMaxPlayers() % 2 == 0)) {
        //    return Optional.of("The max player amount must be dividable by 2 to ensure balanced game-play.");
        //}

        //--- @TODO Load arena from database, if it does exist, otherwise ignore(?) the call. I don't know yet.

        this.arenas.put(arena.getIdentifier(), arena);

        return Optional.empty();
    }

    /**
     * <p>
     *    Returns the arena to the corresponding id. It is empty if no arena with the given id exists.
     * </p>
     * @param id
     * @return
     */
    public Optional<Arena> get(int id) {
        return Optional.ofNullable(this.arenas.get(id));
    }

    /**
     * <p>
     *    Returns a recommended arena id by checking what IDs are available.
     * </p>
     *
     * @return
     */
    public int getRecommendedId() {
        int i = 0;
        for(; i < arenas.size(); i++) {
            if(this.get(i).isPresent()) {
                continue;
            }

            return i;
        }

        return i;
    }

    @Override
    public void setup() {
        ToB.getLogger().info("ArenaManager - Successfully completed the setup.");
    }

    @Override
    public void shutdown() {
        ToB.getLogger().info("ArenaManager - Shutdown down.");
    }
}
