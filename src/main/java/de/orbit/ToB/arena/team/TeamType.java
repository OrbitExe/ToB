package de.orbit.ToB.arena.team;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.util.Color;

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

    /**
     * The opposite team.
     *
     * @return
     */
    TeamType opposite();

    /**
     *
     * Gives the {@link TextColor} related {@link Color}.
     *
     * @return
     */
    Color transformColor();

}