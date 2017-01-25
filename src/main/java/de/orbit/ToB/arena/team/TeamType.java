package de.orbit.ToB.arena.team;

import org.spongepowered.api.text.format.TextColor;

public interface TeamType {

    /**
     * The name of the team used in e.g.: chat on signs and so on.
     *
     * @return
     */
    String displayName();

    /**
     * The color of the team used in e.g: chat on signs and so on.
     *
     * @return
     */
    TextColor color();

}