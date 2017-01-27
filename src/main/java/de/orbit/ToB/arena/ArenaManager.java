package de.orbit.ToB.arena;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.orbit.ToB.Component;
import de.orbit.ToB.ToB;
import de.orbit.ToB.arena.team.TeamTypes;
import de.orbit.ToB.classes.GameClass;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

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

    public String serialize() {

        JsonArray arenaObjects = new JsonArray();

        this.arenas.forEach((k, arena) -> {

            JsonObject object = new JsonObject();

            //--- Simple Data
            object.addProperty("identifier", arena.getIdentifier());
            object.addProperty("maxPlayers", arena.getMaxPlayers());

            //--- All Points
            JsonObject points = new JsonObject();
            points.add("lobbyPoint", serializeLocation(arena.getLobbyPoint()));
            points.add("redPoint", serializeLocation(arena.getSpawnPoint(TeamTypes.RED)));
            points.add("bluePoint", serializeLocation(arena.getSpawnPoint(TeamTypes.BLUE)));

            object.add("locations", points);

            //--- All Buttons
            JsonObject buttons = new JsonObject();
            buttons.add("redButton", serializeLocation(arena.getButton(TeamTypes.RED)));
            buttons.add("blueButton", serializeLocation(arena.getButton(TeamTypes.BLUE)));

            object.add("locations", buttons);

            //--- All Signs
            JsonArray signs = new JsonArray();

            arena.getSigns().forEach(sign -> {

                JsonObject signObject = new JsonObject();

                signObject.add("location", serializeLocation(sign.getSign().getLocation()));
                signObject.addProperty("signType", sign.getSignType().name());
                signObject.addProperty("teamType", sign.getSignType() == ArenaSignEntry.SignType.CLASS ? sign.getTeam().displayName() : "null");

                if(sign.getSignType() == ArenaSignEntry.SignType.CLASS) {
                    signObject.addProperty("content", ((GameClass) sign.getContent()).displayName());
                } else {
                    signObject.addProperty("content", "null");
                }

                signs.add(signObject);

            });

            object.add("signs", signs);

            //--- All plates
            JsonArray plates = new JsonArray();
            arena.getPlates().forEach(e -> {

                JsonObject plateObject = new JsonObject();

                plateObject.addProperty("team", e.getTeam().displayName());
                plateObject.add("location", serializeLocation(e.getLocation()));

                plates.add(plateObject);

            });
            object.add("plates", plates);

            //--- Add to the list
            arenaObjects.add(object);

        });

        return new Gson().toJson(arenaObjects);

    }

    @Override
    public void setup() {
        ToB.getLogger().info("ArenaManager - Successfully completed the setup.");
    }

    @Override
    public void shutdown() {
        ToB.getLogger().info("ArenaManager - Shutdown down.");
    }

    private static JsonObject serializeLocation(Location<World> location) {
        JsonObject object = new JsonObject();
        object.addProperty("identifier", location.getExtent().getUniqueId().toString());
        object.addProperty("x", location.getBlockX());
        object.addProperty("x", location.getBlockY());
        object.addProperty("y", location.getBlockZ());
        return object;
    }


}
