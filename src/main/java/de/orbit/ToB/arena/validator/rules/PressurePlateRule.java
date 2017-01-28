package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.team.TeamType;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
import de.orbit.ToB.arena.validator.RuleState;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PressurePlateRule implements Rule {

    @Override
    public Text displayName(DataContainer dataContainer) {
        TeamType teamType = dataContainer.get("team");
        return Text.builder().color(teamType.color())
            .append(
                Text.builder().color(TextColors.WHITE).append(
                    Text.of(
                        String.format("%s - Pressure Plates", teamType.displayName())
                    )
                ).build()
            ).build();
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
                        " from your side."
                ));

            case ERROR: {
                if(arena.getMaxPlayers() < 2) {
                    return Text.of(String.format(
                        "You have to set the value of max player value to at least 2 before this rule can be applied. The" +
                            " current value is %d.",
                        arena.getMaxPlayers()
                    ));
                }

                return Text.of(String.format(
                    "You need to set %d plates for team %s in the arena. You have currently %d added for this team so far.",
                    (arena.getMaxPlayers() / 2 - 1),
                    teamType.displayName(),
                    (arena.getPlates(teamType).size())
                ));
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
