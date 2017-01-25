package de.orbit.ToB.events;

import de.orbit.ToB.arena.Arena;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

/**
 * <p>
 *    The ArenaJoiningEvent gets called IF a player tries to join an arena, but has not been added yet to it.
 * </p>
 */
public class ArenaJoiningEvent implements Event, Cancellable {

    private Cause cause;

    private Arena arena;
    private Player player;

    private boolean isCancelled  = false;

    public ArenaJoiningEvent(Arena arena, Player player) {
        this.arena = arena;
        this.player = player;

        this.cause = cause;
    }

    /**
     * <p>
     *    The player who wants to join.
     * </p>
     *
     * @return
     */
    public Arena getArena() {
        return this.arena;
    }

    /**
     * <p>
     *    The arena the player wants to join
     * </p>
     *
     * @return
     */
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public Cause getCause() {
        return Cause.source(this.arena).build();
    }

}
