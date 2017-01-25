package de.orbit.ToB.arena;

import de.orbit.ToB.arena.team.TeamType;
import org.spongepowered.api.block.tileentity.Sign;

public class ArenaSignEntry {

    private Sign sign;
    private Arena.SignType signType;
    private TeamType team;

    public ArenaSignEntry(Sign sign, Arena.SignType signType, TeamType team) {
        this.sign = sign;
        this.signType = signType;
        this.team = team;
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

    /**
     * <p>
     *     The sign type.
     * </p>
     *
     * @return
     */
    public Arena.SignType getSignType() {
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

}
