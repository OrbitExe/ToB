package de.orbit.ToB.arena;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.living.player.Player;

public class ArenaPlayer {

    private final Arena arena;
    private final Player player;

    private final DataContainer backup;

    public ArenaPlayer(Player player, Arena arena) {
        this.player = player;
        this.arena = arena;

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
     * Gives the arena instance the player is currently a part of.
     *
     * @return
     */
    public Arena getArena() {
        return this.arena;
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
