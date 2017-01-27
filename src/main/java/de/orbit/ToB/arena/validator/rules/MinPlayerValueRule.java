package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
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
        return Text.of(
                "The player value needs to be at least 2."
        );
    }

    @Override
    public boolean validate(DataContainer dataContainer) {
        return (dataContainer.<Arena>get("arena").getMaxPlayers() >= 2);
    }
}

