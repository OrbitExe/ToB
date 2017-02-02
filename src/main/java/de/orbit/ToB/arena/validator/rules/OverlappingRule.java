package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.ToB;
import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.ArenaManager;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
import de.orbit.ToB.arena.validator.RuleState;
import org.spongepowered.api.text.Text;

public class OverlappingRule implements Rule {

    @Override
    public Text displayName(DataContainer dataContainer) {
        return Text.of("Arena Overlapping");
    }

    @Override
    public Text description(DataContainer dataContainer) {

        RuleState ruleState = this.validate(dataContainer);

        switch (ruleState) {

            case ACCEPTABLE:
                return Text.of(
                    "You have to set the min and max position of the area before this rule can be applied."
                );

            case FULFILLED:
                return Text.of(
                    "The area of the arena doesn't overlap with any other arena."
                );

            case ERROR:
                return Text.of(
                    "The area of this arena intersects with another arena."
                );

        }

        throw new IllegalArgumentException();

    }

    @Override
    public RuleState validate(DataContainer dataContainer) {

        ArenaManager arenaManager = ToB.get(ArenaManager.class);
        Arena arena = dataContainer.get("arena");

        if(arena.getAreaMin() == null || arena.getAreaMax() == null) {
            return RuleState.ACCEPTABLE;
        }

        for(Arena e : arenaManager.getArenas()) {
            if(arena == e) {
                continue;
            }

            if(e.overlaps(arena)) {
                return RuleState.ERROR;
            }
        }

        return RuleState.FULFILLED;

    }

}
