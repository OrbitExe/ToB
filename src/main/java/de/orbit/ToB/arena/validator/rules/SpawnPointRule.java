package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.team.TeamType;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
import de.orbit.ToB.arena.validator.RuleState;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class SpawnPointRule implements Rule {

    @Override
    public Text displayName(DataContainer dataContainer) {
        TeamType teamType = dataContainer.get("team");

        if(teamType == null) {
            return Text.of(
                "Lobby Point"
            );
        }

        return Text.builder().color(teamType.color())
            .append(
                Text.builder().color(TextColors.WHITE).append(
                    Text.of(
                        String.format("%s - Spawn Point", teamType.displayName())
                    )
                ).build()
            ).build();
    }

    @Override
    public Text description(DataContainer dataContainer) {
        TeamType teamType = dataContainer.get("team");
        Arena arena = dataContainer.get("arena");

        RuleState ruleState = this.validate(dataContainer);

        if(teamType == null) {
            switch (ruleState) {

                case ACCEPTABLE:
                case FULFILLED:
                    return Text.of(String.format(
                        "You have set the lobby spawn point for arena %d.",
                            arena.getIdentifier()
                    ));

                case ERROR:
                    return Text.of(String.format(
                        "You haven't set the lobby spawn point for arena %d yet.",
                            arena.getIdentifier()
                    ));
            }

        }

        switch (ruleState) {

            case ACCEPTABLE:
            case FULFILLED:
                return Text.of(String.format(
                    "You have set the spawn point for team %s in arena %d.",
                        teamType.displayName(),
                        arena.getIdentifier()
                ));

            case ERROR:
                return Text.of(String.format(
                    "You haven't set the spawn point for team %s in arena %d yet.",
                    teamType.displayName(),
                    arena.getIdentifier()
                ));
        }

        throw new IllegalArgumentException();

    }

    @Override
    public RuleState validate(DataContainer dataContainer) {
        Arena arena = dataContainer.get("arena");
        TeamType teamType = dataContainer.get("team");

        return (teamType == null ? !(arena.getLobbyPoint() == null) : !(arena.getSpawnPoint(teamType) == null)) ? RuleState.FULFILLED : RuleState.ERROR;
    }

}
