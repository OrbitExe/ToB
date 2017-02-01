package de.orbit.ToB.events;

import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.states.ArenaState;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

/**
 * <p>
 *    This event gets called if an arena is changing its arena state.
 * </p>
 */
public class ArenaStateChangingEvent extends AbstractEvent {

    private Arena arena;

    private ArenaState current;
    private ArenaState next;

    public ArenaStateChangingEvent(Arena arena, ArenaState current, ArenaState next) {}

    /**
     * <p>
     *    The arena which the change is applied to.
     * </p>
     *
     * @return
     */
    public Arena getArena() {
        return this.arena;
    }

    /**
     * <p>
     *    The current state of the arena before the change gets applied.
     * </p>
     *
     * @return
     */
    public ArenaState getCurrent() {
        return this.current;
    }

    /**
     * <p>
     *     The new state of the arena after the change got applied.
     * </p>
     * @return
     */
    public ArenaState getNext() {
        return this.next;
    }

    @Override
    public Cause getCause() {
        return Cause.source(this.arena).build();
    }

}
