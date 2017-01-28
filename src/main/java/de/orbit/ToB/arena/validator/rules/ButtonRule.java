package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.team.TeamType;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
import de.orbit.ToB.arena.validator.RuleState;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ButtonRule implements Rule {

    @Override
    public Text displayName(DataContainer dataContainer) {
        TeamType teamType = dataContainer.get("team");
        return Text.builder().color(teamType.color())
            .append(
                Text.builder().color(TextColors.WHITE).append(
                    Text.of(
                        String.format("%s - Buttons", teamType.displayName())
                    )
            ).build()
        ).build();
    }

    @Override
    public Text description(DataContainer dataContainer) {
        TeamType teamType = dataContainer.get("team");

        RuleState valid = this.validate(dataContainer);

        switch (valid) {

            case ACCEPTABLE:
            case FULFILLED:
                return Text.of(
                    String.format(
                        "You have set the button for team %s.",
                        teamType.displayName()
                    )
                );

            case ERROR:
                return Text.of(
                    String.format(
                        "You need to set the button for team %s.",
                        teamType.displayName()
                    )
                );
        }

        throw new IllegalArgumentException();

    }

    @Override
    public RuleState validate(DataContainer dataContainer) {
        Arena arena = dataContainer.get("arena");
        TeamType teamType = dataContainer.get("team");

        return (!(arena.getButton(teamType) == null)) ? RuleState.FULFILLED : RuleState.ERROR;
    }

}
