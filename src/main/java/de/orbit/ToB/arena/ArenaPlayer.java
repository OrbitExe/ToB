package de.orbit.ToB.arena;

import de.orbit.ToB.arena.team.TeamType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.living.player.Player;

public class ArenaPlayer {

    private final Player player;
    private final Arena arena;
    private TeamType teamType;

    private final DataContainer backup;

    public ArenaPlayer(Player player, Arena arena, TeamType teamType) {
        this.player = player;
        this.arena = arena;
        this.teamType = teamType;

        this.backup = this.player.toContainer().copy();
    }

    /**
     * Gives the corresponding Sponge player.
     *
     * @return
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * The team the player belongs to.
     *
     * @return
     */
    public TeamType getTeamType() {
        return this.teamType;
    }

    /**
     * Gives the arena instance the player is currently a part of.
     *
     * @return
     */
    public Arena getArena() {
        return this.arena;
    }

    /**
     * <p>
     *    Set the team of the player.
     * </p>
     *
     * @param teamType
     */
    public void setTeamType(TeamType teamType) {
       this.teamType = teamType;
    }

    /**
     * Called to reset the players stats to pre-joining moment.
     */
    public void restore() {
        this.player.setRawData(this.backup);
    }

    public void prepare() {
        this.player.getInventory().clear();
    }

}
