package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.ToB;
import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
import de.orbit.ToB.arena.validator.RuleState;
import org.spongepowered.api.text.Text;

public class MinPlayerValueRule implements Rule {

    @Override
    public Text displayName(DataContainer dataContainer) {
        return Text.of(
                "Minimum Player Value"
        );
    }

    @Override
    public Text description(DataContainer dataContainer) {

        RuleState valid = this.validate(dataContainer);
        Arena arena = dataContainer.get("arena");

        switch (valid) {

            case FULFILLED:
                return Text.of(
                    String.format(
                        "You have set the player value to %d. This is perfect, because it is recommended to choose a value " +
                            "in the interval of [8, n + 1].",
                        arena.getMaxPlayers()
                    )
                );

            case ACCEPTABLE:
                return Text.of(
                    String.format(
                        "You have set the player value to %d. This absolutely acceptable, however it is recommended to have " +
                            "a value in the interval of [8, n + 1]. If you have special needs, you can just ignore this warning. " +
                                "The arena will still work as expected.",
                            arena.getMaxPlayers()
                        )
                );

            case ERROR:
                return Text.of(String.format(
                    "The player value needs to be at least %d. The current value is %d.",
                    ToB.ARENA_MIN_PLAYER,
                    arena.getMaxPlayers()
                ));
        }

        throw new IllegalArgumentException();

    }

    @Override
    public RuleState validate(DataContainer dataContainer) {

        Arena arena = dataContainer.get("arena");

        if(arena.getMaxPlayers() >= 8) {
            return RuleState.FULFILLED;
        } else if(arena.getMaxPlayers() >= ToB.ARENA_MIN_PLAYER) {
            return RuleState.ACCEPTABLE;
        }

        return RuleState.ERROR;

    }
}

