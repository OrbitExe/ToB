package de.orbit.ToB.arena;

import com.flowpowered.math.vector.Vector3i;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.orbit.ToB.Component;
import de.orbit.ToB.ToB;
import de.orbit.ToB.arena.states.ArenaStates;
import de.orbit.ToB.arena.team.TeamType;
import de.orbit.ToB.arena.team.TeamTypes;
import de.orbit.ToB.arena.validator.ArenaValidator;
import de.orbit.ToB.classes.GameClass;
import de.orbit.ToB.classes.GameClasses;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.awt.*;
import java.util.*;

public class ArenaManager implements Component {

    private Map<Integer, Arena> arenas = new HashMap<>();

    public ArenaManager() {}

    /**
     * <p>
     *     A collection of all arenas registered in the arena manager.
     * </p>
     *
     * @return
     */
    public Collection<Arena> getArenas() {
        return this.arenas.values();
    }

    /**
     * <p>
     *    Returns a corresponding ArenaPlayer if one exists, if no one exist
     * </p>
     *
     * @param player
     * @return
     */
    public Optional<ArenaPlayer> getPlayer(Player player) {
        Optional<Arena> arena = this.arenas.values().stream().filter(e -> e.getPlayer(player).isPresent()).findAny();

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
     * @return True, if it was successful, otherwise false.
     */
    public Optional<String> add(Arena arena) {

        //--- If the max player amount is high enough
        //if(arena.getMaxPlayers() < 2) {
        //    return Optional.of("An arena must at least hold 2 players.");
        //}

        //--- If the amount of max-players is even.
        //if(!(arena.getMaxPlayers() % 2 == 0)) {
        //    return Optional.of("The max player amount must be dividable by 2 to ensure balanced game-play.");
        //}

        this.arenas.put(arena.getIdentifier(), arena);

        return Optional.empty();
    }

    /**
     * <p>
     *    Returns the arena to the corresponding id. It is empty if no arena with the given id exists.
     * </p>
     *
     * @param id
     * @return
     */
    public Optional<Arena> get(int id) {
        return Optional.ofNullable(this.arenas.get(id));
    }

    /**
     * <p>
     *  Returns the arena containing the provided location.
     * </p>
     *
     * @param location
     * @return
     */
    public Optional<Arena> get(Location<World> location) {

        return this.arenas.values().stream()
                .filter(a -> {

                    Location<World> min = a.getAreaMin();
                    Location<World> max = a.getAreaMax();

                    return (
                            location.getX() >= min.getX() &&
                            location.getX() <= max.getX() &&

                            location.getY() >= min.getY() &&
                            location.getY() <= max.getY() &&

                            location.getZ() >= min.getZ() &&
                            location.getZ() <= max.getZ()
                        );

                }).findFirst();

    }

    public Optional<Arena> get(ArenaSignEntry.SignType signType, Location<World> location) {
        return this.arenas.values().stream()
                .filter(a -> a.existsSign(signType, location))
                .findFirst();
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

    public void unserialize() {

        //@TODO Feeding real data.
        String data = "[{\"identifier\":0,\"maxPlayers\":8,\"bounds\":{\"min\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-315.0,\"y\":55.0,\"z\":-125.0},\"max\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-75.0,\"y\":152.0,\"z\":58.0}},\"towerDimensions\":{\"width\":7.0,\"height\":7.0},\"points\":{\"lobbyPoint\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-185.33558886065958,\"y\":49.0,\"z\":126.84309117653561},\"redPoint\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-84.68050618728505,\"y\":62.0,\"z\":-50.438439188859356},\"bluePoint\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-313.6993395662244,\"y\":62.0,\"z\":-3.001024304757779}},\"buttons\":{\"redButton\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-93.0,\"y\":134.0,\"z\":-18.0},\"blueButton\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-306.0,\"y\":134.0,\"z\":-36.0}},\"signs\":[{\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-86.0,\"y\":62.0,\"z\":-53.0},\"signType\":\"CLASS\",\"teamType\":\"Red\",\"content\":\"Runner\"},{\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-86.0,\"y\":63.0,\"z\":-53.0},\"signType\":\"CLASS\",\"teamType\":\"Red\",\"content\":\"Mercenary\"},{\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-85.0,\"y\":63.0,\"z\":-53.0},\"signType\":\"CLASS\",\"teamType\":\"Red\",\"content\":\"Spy\"},{\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-84.0,\"y\":63.0,\"z\":-53.0},\"signType\":\"CLASS\",\"teamType\":\"Red\",\"content\":\"Builder\"},{\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-84.0,\"y\":62.0,\"z\":-53.0},\"signType\":\"CLASS\",\"teamType\":\"Red\",\"content\":\"Saboteur\"},{\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-313.0,\"y\":62.0,\"z\":-1.0},\"signType\":\"CLASS\",\"teamType\":\"Blue\",\"content\":\"Saboteur\"},{\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-313.0,\"y\":63.0,\"z\":-1.0},\"signType\":\"CLASS\",\"teamType\":\"Blue\",\"content\":\"Runner\"},{\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-314.0,\"y\":63.0,\"z\":-1.0},\"signType\":\"CLASS\",\"teamType\":\"Blue\",\"content\":\"Spy\"},{\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-315.0,\"y\":63.0,\"z\":-1.0},\"signType\":\"CLASS\",\"teamType\":\"Blue\",\"content\":\"Builder\"},{\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-315.0,\"y\":62.0,\"z\":-1.0},\"signType\":\"CLASS\",\"teamType\":\"Blue\",\"content\":\"Mercenary\"},{\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-186.0,\"y\":50.0,\"z\":123.0},\"signType\":\"LOBBY\",\"teamType\":\"null\",\"content\":\"null\"}],\"plates\":[{\"team\":\"Red\",\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-100.70386523936165,\"y\":133.0,\"z\":-12.81992671905358}},{\"team\":\"Red\",\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-106.6768248198569,\"y\":133.0,\"z\":-12.274231253302315}},{\"team\":\"Red\",\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-106.57634599111928,\"y\":133.0,\"z\":-22.41181042994986}},{\"team\":\"Blue\",\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-291.3160560355865,\"y\":133.0,\"z\":-40.5677894558693}},{\"team\":\"Blue\",\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-297.51263319234465,\"y\":133.0,\"z\":-40.416034755159565}},{\"team\":\"Blue\",\"location\":{\"identifier\":\"d5b288e6-d2e3-4130-9916-634c8d0d1f79\",\"x\":-291.629388431396,\"y\":133.0,\"z\":-30.40011662824807}}]}]";

        Gson gson = new Gson();
        JsonArray arenaObjects = gson.fromJson(data, JsonArray.class);

        arenaObjects.forEach(e -> {

            JsonObject object = e.getAsJsonObject();

            //--- Setup Arena instance
            Arena arena = new Arena(object.get("identifier").getAsInt());

            //--- Max Players
            arena.setMaxPlayers(object.get("maxPlayers").getAsInt());

            //--- Arena Bounds
            JsonObject bounds = object.getAsJsonObject("bounds");
            Location<World> min = unserializeLocation(bounds.getAsJsonObject("min"));
            Location<World> max = unserializeLocation(bounds.getAsJsonObject("max"));
            arena.setBoundaries(min, max);

            //--- Sometimes we have some unloaded chunks. We should load them before we try to go any further.
            //@TODO For some reason if we pre-load the chunks at this point this doesn't help, we have to load them exactly
            // when we are checking for the entity (e.g.: sign). - 18.2.2017
            //World world = min.getExtent();
            //for(int x = min.getChunkPosition().getX(); x <= max.getChunkPosition().getX(); x += 16) {
            //    for (int z = min.getChunkPosition().getZ(); z <= max.getChunkPosition().getZ(); z += 16) {
            //        world.loadChunk(new Vector3i(x, 0, z), false);
            //    }
            //}

            //--- Tower Dimensions
            JsonObject towerDimensions = object.getAsJsonObject("towerDimensions");
            arena.setTowerDimension(
                new Dimension(towerDimensions.get("width").getAsInt(), towerDimensions.get("height").getAsInt())
            );

            //--- All Points
            JsonObject locations = object.get("points").getAsJsonObject();
            Location<World> lobbyPoint = unserializeLocation(locations.getAsJsonObject("lobbyPoint"));
            Location<World> redPoint = unserializeLocation(locations.getAsJsonObject("redPoint"));
            Location<World> bluePoint = unserializeLocation(locations.getAsJsonObject("bluePoint"));

            arena.setLobbyPoint(lobbyPoint);
            arena.setSpawnPoint(TeamTypes.RED, redPoint);
            arena.setSpawnPoint(TeamTypes.BLUE, bluePoint);

            //--- All Buttons
            JsonObject buttons = object.getAsJsonObject("buttons");
            Location<World> redButton = unserializeLocation(buttons.getAsJsonObject("redButton"));
            Location<World> blueButton = unserializeLocation(buttons.getAsJsonObject("blueButton"));

            arena.setButtonPoint(TeamTypes.RED, redButton);
            arena.setButtonPoint(TeamTypes.BLUE, blueButton);

            //--- All Signs
            JsonArray signs = object.getAsJsonArray("signs");
            signs.forEach(element -> {

                JsonObject sign = element.getAsJsonObject();

                Location<World> location = unserializeLocation(sign.getAsJsonObject("location"));
                ArenaSignEntry.SignType signType = ArenaSignEntry.SignType.valueOf(sign.get("signType").getAsString());

                // Team Type
                TeamType teamType = null;
                String teamTypeValue = sign.get("teamType").getAsString();
                if(!(teamTypeValue.equalsIgnoreCase("null"))) {
                    teamType = TeamTypes.toTeam(teamTypeValue).get();
                }

                // Content
                GameClass content = null;
                if(signType == ArenaSignEntry.SignType.CLASS) {
                    content = GameClasses.toClass(sign.get("content").getAsString()).get();
                }

                //--- Pre-loading the chunk
                location.getExtent().loadChunk(location.getChunkPosition(), false);
                if(location.getTileEntity().isPresent() && location.getTileEntity().get() instanceof Sign) {
                    arena.addSign(signType, teamType, (Sign) location.getTileEntity().get(), content);
                }

            });

            //--- All plates
            JsonArray plates = object.getAsJsonArray("plates");
            plates.forEach(plate -> {

                JsonObject plateObject = plate.getAsJsonObject();

                Location<World> location = unserializeLocation(plateObject.getAsJsonObject("location"));
                TeamType teamType = TeamTypes.toTeam(plateObject.get("team").getAsString()).get();

                arena.addPlate(location, teamType);

            });

            //--- Validate
            ArenaValidator validator = new ArenaValidator(arena);
            validator.validate();

            //if(validator.isValid()) {
                this.add(arena);
                arena.changeState(ArenaStates.WAITING);
                ToB.getLogger().info(String.format(
                    "Successfully loaded and validated arena %d.",
                        arena.getIdentifier()
                ));
            /*} else {
                //@TODO Show all errors or give player access to the errors
                ToB.getLogger().error(String.format(
                    "Failed to load arena %d.",
                        arena.getIdentifier()
                ));
            }*/


        });


    }

    public String serialize() {

        JsonArray arenaObjects = new JsonArray();

        this.arenas.forEach((k, arena) -> {

            JsonObject object = new JsonObject();

            //--- Simple Data
            object.addProperty("identifier", arena.getIdentifier());
            object.addProperty("maxPlayers", arena.getMaxPlayers());

            //--- Bounds
            JsonObject bounds = new JsonObject();
            bounds.add("min", serializeLocation(arena.getAreaMin()));
            bounds.add("max", serializeLocation(arena.getAreaMax()));

            object.add("bounds", bounds);

            //--- Tower Dimensions
            JsonObject towerDimensions = new JsonObject();
            towerDimensions.addProperty("width", arena.getTowerDimension().getWidth());
            towerDimensions.addProperty("height", arena.getTowerDimension().getHeight());

            object.add("towerDimensions", towerDimensions);

            //--- All Points
            JsonObject points = new JsonObject();
            points.add("lobbyPoint", serializeLocation(arena.getLobbyPoint()));
            points.add("redPoint", serializeLocation(arena.getSpawnPoint(TeamTypes.RED)));
            points.add("bluePoint", serializeLocation(arena.getSpawnPoint(TeamTypes.BLUE)));

            object.add("points", points);

            //--- All Buttons
            JsonObject buttons = new JsonObject();
            buttons.add("redButton", serializeLocation(arena.getButton(TeamTypes.RED)));
            buttons.add("blueButton", serializeLocation(arena.getButton(TeamTypes.BLUE)));

            object.add("buttons", buttons);

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
        object.addProperty("x", location.getX());
        object.addProperty("y", location.getY());
        object.addProperty("z", location.getZ());
        return object;
    }

    private static Location<World> unserializeLocation(JsonObject object) {
        return new Location<>(
                Sponge.getServer().getWorld(UUID.fromString(object.get("identifier").getAsString())).get(),
                object.get("x").getAsDouble(),
                object.get("y").getAsDouble(),
                object.get("z").getAsDouble()
        );
    }


}
