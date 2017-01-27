package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.team.TeamType;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
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

        if(teamType == null) {
            return Text.of(
                "You need to have a lobby spawn point."
            );
        }

        return Text.of(
            String.format(
                "You need to set the button for team %s.",
                teamType.displayName()
            )
        );
    }

    @Override
    public boolean validate(DataContainer dataContainer) {
        Arena arena = dataContainer.get("arena");
        TeamType teamType = dataContainer.get("team");

        return (teamType == null ? !(arena.getLobbyPoint() == null) : !(arena.getSpawnPoint(teamType) == null));
    }

}
