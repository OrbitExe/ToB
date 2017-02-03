package de.orbit.ToB.arena.team;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public enum TeamTypes implements TeamType {

    RED {
        @Override
        public String displayName() {
            return "Red";
        }

        @Override
        public TextColor color() {
            return TextColors.RED;
        }

        @Override
        public TeamType opposite() {
            return TeamTypes.BLUE;
        }
    },
    BLUE {
        @Override
        public String displayName() {
            return "Blue";
        }

        @Override
        public TextColor color() {
            return TextColors.AQUA;
        }

        @Override
        public TeamType opposite() {
            return TeamTypes.RED;
        }
    };

    /**
     * Converts the color you find on a sign or somewhere else to its corresponding team.
     * @param color The color you wanna convert to team.
     *
     * @return
     */
    public static Optional<TeamType> toTeam(TextColor color) {
        for(TeamType entry : TeamTypes.values()) {
            if(entry.color() == color) {
                return Optional.of(entry);
            }
        }

        return Optional.empty();
    }

    /**
     * Converts the name of a team you find on a sign or somewhere else to its corresponding team.
     * @param name The name you wanna convert to team.
     *
     * @return
     */
    public static Optional<TeamType> toTeam(String name) {
        for(TeamType entry : TeamTypes.values()) {
            if(entry.displayName().equalsIgnoreCase(name)) {
                return Optional.of(entry);
            }
        }

        return Optional.empty();
    }

}
