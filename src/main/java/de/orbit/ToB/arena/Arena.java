package de.orbit.ToB.arena;

import de.orbit.ToB.ToB;
import de.orbit.ToB.arena.states.ArenaState;
import de.orbit.ToB.arena.states.ArenaStates;
import de.orbit.ToB.arena.team.TeamType;
import de.orbit.ToB.arena.team.TeamTypes;
import de.orbit.ToB.classes.GameClass;
import de.orbit.ToB.classes.GameClasses;
import de.orbit.ToB.events.ArenaJoiningEvent;
import de.orbit.ToB.events.ArenaStateChangingEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class Arena {

    private final int identifier;

    private ArenaState arenaState = ArenaStates.DISABLED;

    private List<ArenaPlayer> players = new ArrayList<>();

    private List<ArenaSignEntry> signs = new ArrayList<>();
    private List<ArenaPlateEntry> pressurePlates = new ArrayList<>();

    private int maxPlayers;

    private Location<World> lobbyPoint;
    private Location<World> redPoint;
    private Location<World> bluePoint;

    private Location<World> redButton;
    private Location<World> blueButton;

    public Arena(int identifier) {

        this.identifier = identifier;
    }

    /**
     * <p>
     *     Gives the unique identifier of the arena.
     * </p>
     *
     * @return
     */
    public int getIdentifier() {
        return this.identifier;
    }

    /**
     * <p>
     *    A list containing all players currently inside the arena.
     * </p>
     *
     * @return
     */
    public List<ArenaPlayer> getPlayers() {
        return this.players;
    }

    /**
     * <p>
     *     The current state of the arena.
     * </p>
     *
     * @return
     */
    public ArenaState getArenaState() {
        return this.arenaState;
    }

    /**
     * <p>
     *    Gives the point where all players are supposed to be placed at if they join the arena.
     * </p>
     *
     * @return
     */
    public Location<World> getLobbyPoint() {
        return this.lobbyPoint;
    }

    /**
     * <p>
     *    Gives the spawn point of the provided team.
     * </p>
     *
     * @param teamType
     * @return
     */
    public Location<World> getSpawnPoint(TeamType teamType) {

        if(teamType == TeamTypes.RED) {
            return this.redPoint;
        }

        if(teamType == TeamTypes.BLUE) {
            return this.bluePoint;
        }

        throw new IllegalArgumentException("The provided team is unknown.");

    }

    /**
     * <p>
     *    Gives the button point of the provided team.
     * </p>
     *
     * @param teamType
     * @return
     */
    public Location<World> getButton(TeamType teamType) {

        if(teamType == TeamTypes.RED) {
            return this.redButton;
        }

        if(teamType == TeamTypes.BLUE) {
            return this.blueButton;
        }

        throw new IllegalArgumentException("The provided team is unknown.");

    }

    /**
     * <p>
     *    Gives the plates for the provided team.
     * </p>
     *
     * @param teamType
     * @return
     */
    public List<ArenaPlateEntry> getPlates(TeamType teamType) {
        return this.pressurePlates.stream().filter(e -> e.getTeam() == teamType).collect(Collectors.toList());
    }

    /**
     * <p>
     *    Gives all plates of the arena.
     * </p>
     *
     * @return
     */
    public List<ArenaPlateEntry> getPlates() {
        return this.pressurePlates.stream().collect(Collectors.toList());
    }

    /**
     * <p>
     *     The max amount of players this arena
     * </p>
     *
     * @return
     */
    public int getMaxPlayers() {
        return this.maxPlayers;
    }


    /**
     * <p>
     *    Gives a list of all related signs.
     * </p>
     * @return
     */
    public List<ArenaSignEntry> getSigns() {
        return this.signs;
    }

    /**
     * <p>
     *    Gives a list for the related sign type.
     * </p>
     * @return
     */
    public List<ArenaSignEntry> getGameClassesSigns(TeamType teamType, boolean distinct) {
        List<GameClass> gameClasses = new ArrayList<>();
        return this.signs.stream().
                filter(e -> e.getSignType() == ArenaSignEntry.SignType.CLASS && e.getTeam() == teamType)
                .filter(e -> {

                    if(!(distinct)) {
                        return true;
                    }

                    if(gameClasses.contains(e.getContent())) {
                        return false;
                    }

                    gameClasses.add((GameClass) e.getContent());
                    return true;
                })
                .collect(Collectors.toList());
    }


    /**
     * <p>
     *    Adds a new player to the arena if possible. This can be cancelled by listing to the {@link ArenaJoiningEvent}
     *    and cancelling it if necessary.
     * </p>
     *
     * @param player
     */
    public void add(Player player) {

        ArenaJoiningEvent event = new ArenaJoiningEvent(this, player);
        Sponge.getEventManager().post(event);

        if(event.isCancelled()) {
            //@TODO Player couldn't join
            return;
        }

        //@TODO maybe do mare than just adding, probably calling also its setup method
        this.players.add(new ArenaPlayer(player, this));

    }

    /**
     * <p>
     *    Sets the state of the arena and fires of a {@link ArenaStateChangingEvent} which cannot be cancelled or modified.
     * </p>
     *
     * @param arenaState
     */
    public void setArenaState(ArenaState arenaState) {

        Sponge.getEventManager().post(
                new ArenaStateChangingEvent(this, this.arenaState, arenaState)
        );

        this.updateSigns();

        this.arenaState = arenaState;

    }

    /**
     * <p>
     *    Gives the spawn point of the provided team.
     * </p>
     *
     * @param teamType
     * @return
     */
    public void setSpawnPoint(TeamType teamType, Location<World> location) {

        if(teamType == TeamTypes.RED) {
            this.redPoint = location;
            return;
        }

        if(teamType == TeamTypes.BLUE) {
            this.bluePoint = location;
            return;
        }

        throw new IllegalArgumentException("The provided team is unknown.");

    }

    /**
     * <p>
     *    Gives the button point of the provided team.
     * </p>
     *
     * @param teamType
     * @return
     */
    public void setButtonPoint(TeamType teamType, Location<World> location) {

        if(teamType == TeamTypes.RED) {
            this.redButton = location;
            return;
        }

        if(teamType == TeamTypes.BLUE) {
            this.blueButton = location;
            return;
        }

        throw new IllegalArgumentException("The provided team is unknown.");

    }

    /**
     * <p>
     *     Sets the lobby point of the arena.
     * </p>
     *
     * @param lobbyPoint
     */
    public void setLobbyPoint(Location<World> lobbyPoint) {
        this.lobbyPoint = lobbyPoint;
    }

    /**
     * <p>
     *     Sets the max amount of players for this arena.
     * </p>
     *
     * @param maxPlayers
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * <p>
     *    Gives the corresponding {@link ArenaPlayer} if it does exist in the arena, otherwise is the Optional empty.
     * </p>
     *
     * @param player
     * @return
     */
    public Optional<ArenaPlayer> getPlayer(Player player) {
        return this.players.stream().filter(e -> e.getPlayer().equals(player.getUniqueId())).findAny();
    }

    /**
     * <p>
     *    Checks if the player is currently in this arena.
     * </p>
     *
     * @param player
     * @return
     */
    public boolean hasPlayer(Player player) {
        return this.players.stream().anyMatch(e -> e.getPlayer().getUniqueId().equals(player.getUniqueId()));
    }

    /**
     * <p>
     *    Updates currently only all lobby-signs related to the arena.
     * </p>
     */
    public void updateSigns() {
        //--- Update all LOBBY signs
        this.signs.stream().filter(e -> e.getSignType() == ArenaSignEntry.SignType.LOBBY).forEach(e -> {

            Sign sign = e.getSign();
            SignData signData = sign.getSignData();

            signData.setElements(new ArrayList<Text>() {{

                this.add(0, Text.builder().color(TextColors.AQUA).append(Text.of("###############")).build());
                this.add(
                        1,
                        Text.builder().color(Arena.this.getArenaState().color()).append(
                                Text.of(Arena.this.getArenaState().displayName())
                        ).build()
                );
                this.add(
                        2,
                        Text.builder().color(TextColors.AQUA).append(
                                Text.of(String.format("%d/%d", Arena.this.getPlayers().size(), Arena.this.getMaxPlayers()))
                        ).build()
                );
                this.add(3, Text.builder().color(TextColors.AQUA).append(Text.of("###############")).build());

            }});

            //--- Set updated data
            sign.offer(signData);

        });

        //--- Update all CLASS signs
        this.signs.stream().filter(e -> e.getSignType() == ArenaSignEntry.SignType.CLASS).forEach(e -> {

            Sign sign = e.getSign();
            SignData signData = sign.get(SignData.class).get();

            Optional<GameClass> gameClassOptional = GameClasses.toClass(signData.get(2).get().toPlain());

            if(!(gameClassOptional.isPresent())) {
                ToB.getLogger().error(
                    String.format(
                        "Arena - Failed to update the class sign @ %s, because the sign doesn't provide a valid class type: %s.",
                        sign.getLocation().getBlockPosition().toString(),
                        signData.get(2).get().toPlain()
                    )
                );
                return;
            }

            GameClass gameClass = gameClassOptional.get();

            signData.setElements(new ArrayList<Text>() {{

                this.add(0, Text.builder().color(e.getTeam().color()).append(Text.of("###############")).build());
                this.add(
                        1,
                        Text.builder()
                            .append(Text.builder().color(TextColors.GOLD).append(Text.of("[")).build())
                            .append(Text.builder().color(TextColors.AQUA).append(Text.of("Class")).build())
                            .append(Text.builder().color(TextColors.GOLD).append(Text.of("]")).build())
                        .build()
                );
                this.add(2, Text.builder().color(TextColors.GOLD).append(Text.of(gameClass.displayName())).build()
                );
                this.add(3, Text.builder().color(e.getTeam().color()).append(Text.of("###############")).build());

            }});

            //--- Set updated data
            sign.offer(signData);

        });
    }

    /**
     * <p>
     *    Adds a sign to the arena. It will automatically receive updates from it.
     * </p>
     *
     * @param signType
     * @param teamType
     * @param sign
     * @param content
     */
    public <T> void addSign(ArenaSignEntry.SignType signType, TeamType teamType, Sign sign, T content) {
        //@TODO check if the sign already exists
        this.signs.add(new ArenaSignEntry<>(this, sign, signType, teamType, content));
    }

    /**
     * <p>
     *    Adds a plate to the arena.
     * </p>
     *
     * @param location
     * @param teamType
     */
    public void addPlate(Location<World> location, TeamType teamType) {
        //@TODO check if it already exits
        this.pressurePlates.add(new ArenaPlateEntry(location, teamType));
    }

    /**
     * <p>
     *    Removes the sign from this arena.
     * </p>
     *
     * @param arenaSignEntry
     */
    public void removeSign(ArenaSignEntry arenaSignEntry) {
        this.signs.remove(arenaSignEntry);
    }

    /**
     * <p>
     *    Checks if a sign exists in this arena filtered by the location and the sign type.
     * </p>
     *
     * @param signType
     * @param location
     * @return
     */
    public boolean existsSign(ArenaSignEntry.SignType signType, Location<World> location) {
        return this.signs.stream().anyMatch(e -> e.getSignType() == signType && e.getSign().getLocation().equals(location));
    }

    /**
     * <p>
     *    Creates from both locations the most minimal location that is possible.
     * </p>
     *
     * @param a
     * @param b
     * @return
     */
    private static Location<World> min(Location<World> a, Location<World> b) {

        if(!(a.getExtent().equals(b.getExtent()))) {
            throw new IllegalArgumentException("The worlds must be equal.");
        }

        int minX = Math.min(a.getBlockX(), b.getBlockX());
        int minY = Math.min(a.getBlockY(), b.getBlockY());
        int minZ = Math.min(a.getBlockZ(), b.getBlockZ());

        return new Location<>(a.getExtent(), minX, minY, minZ);
    }

    /**
     * <p>
     *    Creates from both locations the most maximal location that is possible.
     * </p>
     *
     * @param a
     * @param b
     * @return
     */
    private static Location<World> max(Location<World> a, Location<World> b) {
        int maxX = Math.max(a.getBlockX(), b.getBlockX());
        int maxY = Math.max(a.getBlockY(), b.getBlockY());
        int maxZ = Math.max(a.getBlockZ(), b.getBlockZ());

        return new Location<>(a.getExtent(), maxX, maxY, maxZ);
    }

}
