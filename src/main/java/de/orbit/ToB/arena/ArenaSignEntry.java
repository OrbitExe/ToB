package de.orbit.ToB.arena;

import de.orbit.ToB.arena.team.TeamType;
import org.spongepowered.api.block.tileentity.Sign;

public class ArenaSignEntry<T> {

    private T content;

    private Sign sign;
    private SignType signType;
    private TeamType team;

    public ArenaSignEntry(Sign sign, SignType signType, TeamType team, T content) {
        this.sign = sign;
        this.signType = signType;
        this.team = team;
        this.content = content;
    }


    /**
     * <p>
     *    The sign belonging to the entry.
     * </p>
     *
     * @return
     */
    public Sign getSign() {
        return this.sign;
    }


    public T getContent() {
        return this.content;
    }

    /**
     * <p>
     *     The sign type.
     * </p>
     *
     * @return
     */
    public SignType getSignType() {
        return this.signType;
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

    public enum SignType {

        LOBBY,
        CLASS;

    }

}
