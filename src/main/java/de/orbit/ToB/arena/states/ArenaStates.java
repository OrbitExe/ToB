package de.orbit.ToB.arena.states;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public enum ArenaStates implements ArenaState {

    DISABLED {

        @Override
        public String displayName() {
            return "Disabled";
        }

        @Override
        public TextColor color() {
            return TextColors.RED;
        }

    },
    MAINTENANCE {

        @Override
        public String displayName() {
            return "Maintenance";
        }

        @Override
        public TextColor color() {
            return TextColors.RED;
        }

    },
    WAITING {

        @Override
        public String displayName() {
            return "Waiting...";
        }

        @Override
        public TextColor color() {
            return TextColors.GOLD;
        }

    },
    COUNTDOWN {

        @Override
        public String displayName() {
            return "Countdown";
        }

        @Override
        public TextColor color() {
            return TextColors.GOLD;
        }

    },
    STARTED {

        @Override
        public String displayName() {
            return "In Game";
        }

        @Override
        public TextColor color() {
            return TextColors.AQUA;
        }

    },
    WON {

        @Override
        public String displayName() {
            return "Won";
        }

        @Override
        public TextColor color() {
            return TextColors.GREEN;
        }

    },
    RESTARTING {

        @Override
        public String displayName() {
            return "Restarting";
        }

        @Override
        public TextColor color() {
            return TextColors.DARK_RED;
        }

    },
    ERROR {

        @Override
        public String displayName() {
            return "Error";
        }

        @Override
        public TextColor color() {
            return TextColors.DARK_RED;
        }

    }

}
