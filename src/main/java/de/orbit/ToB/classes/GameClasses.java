package de.orbit.ToB.classes;

import de.orbit.ToB.arena.ArenaPlayer;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public enum GameClasses implements GameClass {

    MERCENARY {

        @Override
        public String displayName() {
            return "Mercenary";
        }

        @Override
        public double defaultFactor() {
            return 0.2D;
        }

        @Override
        public void apply(ArenaPlayer player) {
            throw new NotImplementedException("Not implemented yet.");
        }

    },
    BUILDER {

        @Override
        public String displayName() {
            return "Builder";
        }

        @Override
        public double defaultFactor() {
            return 0.2D;
        }

        @Override
        public void apply(ArenaPlayer player) {
            throw new NotImplementedException("Not implemented yet.");
        }
    },
    SPY {

        @Override
        public String displayName() {
            return "Spy";
        }

        @Override
        public double defaultFactor() {
            return 0.2D;
        }

        @Override
        public void apply(ArenaPlayer player) {
            throw new NotImplementedException("Not implemented yet.");
        }

    },
    RUNNER {

        @Override
        public String displayName() {
            return "Runner";
        }

        @Override
        public double defaultFactor() {
            return 0.2D;
        }

        @Override
        public void apply(ArenaPlayer player) {
            throw new NotImplementedException("Not implemented yet.");
        }

    },
    SABOTEUR {

        @Override
        public String displayName() {
            return "Saboteur";
        }

        @Override
        public double defaultFactor() {
            return 0.2D;
        }

        @Override
        public void apply(ArenaPlayer player) {
            throw new NotImplementedException("Not implemented yet.");
        }

    };

    /**
     * Gives the GameClass corresponding to its name.
     *
     * @param name
     * @return
     */
    public static Optional<GameClass> toClass(String name) {

        for(GameClass entry : GameClasses.values()) {
            if(entry.displayName().equalsIgnoreCase(name)) {
                return Optional.of(entry);
            }
        }

        return Optional.empty();

    }

    @Override
    public String toString() {
        return StringUtils.capitalize(this.name().toLowerCase());
    }

}