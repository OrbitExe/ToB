package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
import de.orbit.ToB.arena.validator.RuleState;
import org.spongepowered.api.text.Text;

public class PlayerDividableRule implements Rule {

    @Override
    public Text displayName(DataContainer dataContainer) {
        return Text.of(
                "Player Value Dividable"
        );
    }

    @Override
    public Text description(DataContainer dataContainer) {

        Arena arena = dataContainer.get("arena");

        RuleState ruleState = this.validate(dataContainer);

        switch (ruleState) {

            case ACCEPTABLE:
            case FULFILLED:
                return Text.of(String.format(
                    "You have successfully selected a player value that is dividable by 2. The current value is %d.",
                    arena.getMaxPlayers()
                ));

            case ERROR:
                return Text.of(String.format(
                    "The player value has to be dividable by 2 to provide more fairness in all games in the best case. " +
                        "This also improves the overall game balance and experience. The current value is %d.",
                    arena.getMaxPlayers()
                ));
        }

        throw new IllegalArgumentException();

    }

    @Override
    public RuleState validate(DataContainer dataContainer) {
        Arena arena = dataContainer.<Arena>get("arena");
        return (arena.getMaxPlayers() % 2 == 0 && !(arena.getMaxPlayers() == 0))  ? RuleState.FULFILLED : RuleState.ERROR;
    }

}
