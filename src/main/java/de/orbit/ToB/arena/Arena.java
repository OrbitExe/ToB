package de.orbit.ToB.arena;

import de.orbit.ToB.MessageHandler;
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
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Firework;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Arena {

    private static final Random random = new Random();

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

    private Location<World> areaMin;
    private Location<World> areaMax;

    private Dimension towerDimension;

    private List<BlockSnapshot> backup = new ArrayList<>();

    //--- Tasks
    private Task lobbyCountdown = null;

    //--- Winner
    private TeamType winnerTeam = null;

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
     *    Gives the max boundary point of the arena.
     * </p>
     *
     * @return
     */
    public Location<World> getAreaMax() {
        return this.areaMax;
    }

    /**
     * <p>
     *    Gives the min boundary point of the arena.
     * </p>
     *
     * @return
     */
    public Location<World> getAreaMin() {
        return this.areaMin;
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
     *    Gives the ground tower dimension in blocks. <br />
     *    n * m
     * </p>
     *
     * @return
     */
    public Dimension getTowerDimension() {
        return this.towerDimension;
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
     *    Gives a list for the related sign type. - If the search is distinct it only returns a maximum of one sign per
     *    {@link GameClass}. This can be used to check for what GameClasses signs exist more easily.
     * </p>
     *
     * @param teamType
     * @param distinct
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
                }).collect(Collectors.toList());
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
        return this.players.stream().filter(e -> e.getPlayer().getUniqueId().equals(player.getUniqueId())).findAny();
    }

    /**
     * <p>
     *     Returns all players belonging to the provided team.
     * </p>
     *
     * @param teamType
     * @return
     */
    public List<ArenaPlayer> getTeam(TeamType teamType) {
        return this.players.stream().filter(e -> e.getTeamType() == teamType).collect(Collectors.toList());
    }

    /**
     * <p>
     *    Adds a new player to the arena if possible. This can be cancelled by listing to the {@link ArenaJoiningEvent}
     *    and cancelling it if necessary.
     * </p>
     *
     * @param player
     * @return true, if successful, otherwise false.
     */
    public boolean add(Player player) {

        ArenaJoiningEvent event = new ArenaJoiningEvent(this, player);
        event.setCancelled(
            (
                !(this.arenaState == ArenaStates.COUNTDOWN) &&
                !(this.arenaState == ArenaStates.WAITING)
            ) ||
            this.players.size() >= this.maxPlayers ||
            ToB.get(ArenaManager.class).getPlayer(player).isPresent()
        );

        Sponge.getEventManager().post(event);


        if(event.isCancelled()) {
            return false;
        }

        //--- Add and store (prepare) the player
        ArenaPlayer arenaPlayer = new ArenaPlayer(player, this, null);
        this.players.add(arenaPlayer);
        arenaPlayer.prepare();

        //--- Update signs
        this.updateSigns();

        //--- Check if we should trigger the countdown
        if(
            (this.players.size() % 2 == 1) &&
            (this.players.size() >= /*this.maxPlayers*/ 2 / 2) &&
            this.is(ArenaStates.WAITING)
        ) {
            this.changeState(ArenaStates.COUNTDOWN);
        }

        return true;

    }

    /**
     * <p>
     *    Checks if the arena is in the provided arena state.
     * </p>
     *
     * @param arenaState The arena state.
     * @return
     */
    public boolean is(ArenaState arenaState) {
        return this.arenaState == arenaState;
    }

    /**
     * <p>
     *    Sets the state of the arena and fires of a {@link ArenaStateChangingEvent} which cannot be cancelled or modified
     *    and it calls {@link Arena#changeState(ArenaState)} to trigger related game mechanics.
     * </p>
     *
     * @param arenaState
     */
    public void changeState(ArenaState arenaState) {

        Sponge.getEventManager().post(
                new ArenaStateChangingEvent(this, this.arenaState, arenaState)
        );

        this.arenaState = arenaState;
        this.handleState(arenaState);
        this.updateSigns();

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
     *    Sets the area boundaries.
     * </p>
     *
     * @param a
     * @param b
     */
    public void setBoundaries(Location<World> a, Location<World> b) {

        if(a == null) {
            this.areaMin = b;
            return;
        }

        if(b == null) {
            this.areaMax = a;
            return;
        }

        this.areaMin = Arena.min(a, b);
        this.areaMax = Arena.max(a, b);
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

                this.add(0, Text.builder().color(TextColors.AQUA).append(Text.of("‾‾‾‾‾‾‾‾‾")).build());
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
        this.pressurePlates.add(new ArenaPlateEntry(this, location, teamType));
    }

    /**
     * <p>
     *    Removes the sign from this arena.
     * </p>
     *
     * @param arenaPlateEntry
     */
    public void removePlate(ArenaPlateEntry arenaPlateEntry) {
        this.pressurePlates.remove(arenaPlateEntry);
    }

    /**
     * <p>
     *    Sets the tower dimension for the arena.
     * </p>
     *
     * @param towerDimension
     */
    public void setTowerDimension(Dimension towerDimension) {
        this.towerDimension = towerDimension;
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
        return this.signs.stream().anyMatch(e -> e.getSignType() == signType && e.getSign().getLocation().getBlockPosition().equals(location.getBlockPosition()));
    }


    /**
     * <p>
     *    Creates a backup of the arena.
     * </p>
     *
     * @param force You can force an update.
     *
     * @return The amount of blocks stored.
     */
    public int createBackup(boolean force) {

        if(!(this.backup.isEmpty()) && !(force)) {
            throw new IllegalStateException("You cannot create a new backup while one already exists. You have to force" +
                    " the backup if you are sure that you wanna do it.");
        }

        for(int x = this.areaMin.getBlockX(); x < this.areaMax.getBlockX(); x++) {
            for(int y = this.areaMin.getBlockY(); y < this.areaMax.getBlockY(); y++) {
                for(int z = this.areaMin.getBlockZ(); z < this.areaMax.getBlockZ(); z++) {
                    this.backup.add(this.areaMin.getExtent().createSnapshot(x, y, z).copy());
                }
            }
        }

        return this.backup.size();
    }

    /**
     * <p>
     *    Restoring it to its latest backup.
     * </p>
     */
    public void restoreBackup() {

        if(this.backup.isEmpty()) {
            throw new IllegalStateException("There is no backup available");
        }

        //@TODO BlockChangeFlag.NONE might not work the best, but updating all with ALL seems to be quiet buggy - further
        // tests required
        this.backup.forEach(e -> e.restore(true, BlockChangeFlag.NONE));
    }

    /**
     * <p>
     *    Checks if the provided arena overlaps with this arena.
     * </p>
     *
     * @param arena
     * @return
     */
    public boolean overlaps(Arena arena) {

        if(arena.getAreaMin() == null || arena.getAreaMax() == null) {
            return false;
        }

        Rectangle2D arenaRec = new Rectangle2D.Double(
                this.areaMin.getX(),
                this.areaMin.getY(),
                (this.areaMax.getX() - this.areaMin.getX()),
                (this.areaMax.getY() - this.areaMin.getY())
        );

        Rectangle2D compareTo = new Rectangle2D.Double(
                arena.getAreaMin().getX(),
                arena.getAreaMin().getY(),
                (arena.getAreaMax().getX() - arena.getAreaMin().getX()),
                (arena.getAreaMax().getY() - arena.getAreaMin().getY())
        );

        return (arenaRec.intersects(compareTo) || arenaRec.contains(compareTo));

    }

    /**
     * <p>
     *     Handles the various {@link ArenaStates}.
     * </p>
     *
     * @param arenaState The arena state you wanna handle.
     */
    private void handleState(ArenaState arenaState) {

        if(arenaState == ArenaStates.WAITING) {

            //--- If we currently have a countdown -> cancel it
            if(!(this.lobbyCountdown == null)) {
                this.lobbyCountdown.cancel();
                this.broadcast("Countdown got cancelled.");
            }

            return;
        }

        if(arenaState == ArenaStates.COUNTDOWN) {
            //@TODO make this adjustable
            final int[] seconds = {60};

            this.lobbyCountdown = Task.builder().interval(1, TimeUnit.SECONDS).execute(e -> {

                if(
                    (seconds[0] % 30 == 0) ||
                    (seconds[0] < 30 && seconds[0] % 5 == 0) ||
                    (seconds[0] < 10)
                )

                this.broadcast("The game starts in %d second(s).", seconds[0]);
                seconds[0]--;

                if(seconds[0] <= 0) {
                    e.cancel();
                    this.changeState(ArenaStates.STARTED);
                }
            }).async().submit(ToB.getInstance());
            return;
        }

        if(arenaState == ArenaStates.STARTED) {

            //--- Set the team for all players & spawn them at their location
            //@TODO We wanna have a ranking system to create more fair teams, however this is far way out and we gonna
            // wait a bit before adding this feature. - 18.2.2017
            TeamType team = TeamTypes.BLUE;

            //--- Mixing it up to make it slightly more random
            Collections.shuffle(this.players);
            for(ArenaPlayer player : this.players) {
                player.setTeamType(team);
                player.getPlayer().setLocation(
                    this.getSpawnPoint(team)
                );
                team = team.opposite();
            }

            return;
        }

        if(arenaState == ArenaStates.RESTARTING) {
            //@TODO Restart.
            return;
        }

        if(arenaState == ArenaStates.WON) {

            //--- Spawning firework
            FireworkEffect effect = FireworkEffect.builder()
                    .color(this.winnerTeam.transformColor())
                    .shape(FireworkShapes.BALL) // @TODO Randomness would be cool, but of course FireworkShapes is not an enum... Doing this "later".
                    .trail(true)
                    .build();
            this.players.forEach(e -> {
                Location<World> loc = e.getPlayer().getLocation();

                Firework firework = (Firework) loc.getExtent().createEntity(
                    EntityTypes.FIREWORK,
                    loc.getPosition()
                );
                firework.offer(Keys.FIREWORK_EFFECTS, Collections.singletonList(effect));
                firework.offer(Keys.FIREWORK_FLIGHT_MODIFIER, 4);
            });

            //--- Broadcasting the message
            this.broadcast("The %s team won the game.", this.winnerTeam.displayName());

            //--- Sending them all back
            //@TODO Wait maybe 10 seconds before sending them back.
            this.players.forEach(ArenaPlayer::restore);
            return;
        }

        if(arenaState == ArenaStates.MAINTENANCE) {

            return;
        }

        if(arenaState == ArenaStates.ERROR) {

            return;
        }

        if(arenaState == ArenaStates.DISABLED) {

            return;
        }

    }

    public void broadcast(TeamType teamType, String message, Object... objects) {

        this.players.stream()
                .filter(e -> (teamType == null || teamType == e.getTeamType()))
                .forEach(e -> ToB.get(MessageHandler.class).send(
                    e.getPlayer(),
                    MessageHandler.Level.INFO,
                    message,
                    objects
                ));

    }

    public void broadcast(TeamType teamType, String message) {
        this.broadcast(teamType, message, new Object[]{});
    }

    public void broadcast(String message) {
        this.broadcast(null, message, new Object[]{});
    }

    public void broadcast(String message, Object... objects) {
        this.broadcast(null, message, objects);
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
