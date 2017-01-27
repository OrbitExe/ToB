package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
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
        return Text.of(
                "The player value has to be dividable by 2 to provide more fairness in all games in the best case. This" +
                        "also improves the overall game balance and experience."
        );
    }

    @Override
    public boolean validate(DataContainer dataContainer) {
        return (dataContainer.<Arena>get("arena").getMaxPlayers() % 2 == 0);
    }

}
