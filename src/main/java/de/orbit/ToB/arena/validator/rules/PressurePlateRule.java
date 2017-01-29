package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.ToB;
import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.team.TeamType;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
import de.orbit.ToB.arena.validator.RuleState;
import org.spongepowered.api.text.Text;

public class PressurePlateRule implements Rule {

    @Override
    public Text displayName(DataContainer dataContainer) {
        TeamType teamType = dataContainer.get("team");
        return Text.builder("Pressure Plates").color(teamType.color()).build();
    }

    @Override
    public Text description(DataContainer dataContainer) {
        Arena arena = dataContainer.get("arena");
        TeamType teamType = dataContainer.get("team");

        RuleState valid = this.validate(dataContainer);

        switch (valid) {

            case FULFILLED:
                return Text.of(String.format(
                    "You have set successfully set %s pressure plates for team %s. This is perfect, no more action required" +
                        " from your side.",
                        (arena.getPlates(teamType).size()),
                        teamType.displayName()
                ));

            case ERROR: {
                if(arena.getMaxPlayers() < ToB.ARENA_MIN_PLAYER) {
                    return Text.of(String.format(
                        "You have to set the value of max player value to at least %d before this rule can be applied. The" +
                            " current value is %d.",
                        ToB.ARENA_MIN_PLAYER,
                        arena.getMaxPlayers()
                    ));
                }

                if((arena.getMaxPlayers() / 2 - 1) - (arena.getPlates(teamType).size()) > 0) {
                    return Text.of(String.format(
                        "You need to set %d plates for team %s in the arena. You have currently %d added for this team so far.",
                        (arena.getMaxPlayers() / 2 - 1),
                        teamType.displayName(),
                        (arena.getPlates(teamType).size())
                    ));
                } else {
                    return Text.of(String.format(
                        "You need to set a maximum of %d plates for team %s in the arena. You have to remove %d plates.",
                        (arena.getMaxPlayers() / 2 - 1),
                        teamType.displayName(),
                        (arena.getPlates(teamType).size() - (arena.getMaxPlayers() / 2 - 1))
                    ));
                }
            }

        }

        throw new IllegalArgumentException();

    }

    @Override
    public RuleState validate(DataContainer dataContainer) {
        Arena arena = dataContainer.get("arena");
        TeamType teamType = dataContainer.get("team");

        return ((arena.getMaxPlayers() / 2 - 1) == arena.getPlates(teamType).size()) ? RuleState.FULFILLED : RuleState.ERROR;
    }

}
