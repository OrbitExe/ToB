package de.orbit.ToB.arena.validator;

import de.orbit.ToB.arena.team.TeamType;
import org.spongepowered.api.text.Text;

public interface Rule {

    Text displayName(DataContainer dataContainer);

    Text description(DataContainer dataContainer);

    boolean validate(DataContainer dataContainer);

}
