package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.ArenaSignEntry;
import de.orbit.ToB.arena.team.TeamType;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
import de.orbit.ToB.arena.validator.RuleState;
import de.orbit.ToB.classes.GameClass;
import de.orbit.ToB.classes.GameClasses;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GameClassesRule implements Rule {

    @Override
    public Text displayName(DataContainer dataContainer) {
        TeamType teamType = dataContainer.get("team");
        return Text.builder().color(teamType.color())
            .append(
                Text.builder().color(TextColors.WHITE).append(
                    Text.of(
                        String.format("%s - Game Classes", teamType.displayName())
                    )
                ).build()
            ).build();
    }

    @Override
    public Text description(DataContainer dataContainer) {
        TeamType teamType = dataContainer.get("team");
        Arena arena = dataContainer.get("arena");

        List<ArenaSignEntry> gameClasses = arena.getGameClassesSigns(teamType, true);
        List<GameClass> all = new LinkedList<>(Arrays.asList(GameClasses.values()));

        gameClasses.forEach(e -> all.remove(e.getContent()));

        if(gameClasses.isEmpty()) {
            return Text.of(
                    String.format(
                    "You need to set game class signs for team %s to let them choose their class. You haven't set a single one " +
                            "yet. These are the available classes: %s.",
                    teamType.displayName(),
                    StringUtils.join(all, ", ")
                )
            );
        } else if(all.isEmpty()) {
            return Text.of(String.format(
                "You have set for all available game classes corresponding signs for team %s.",
                teamType.displayName()
            ));
        }

        return Text.of(
                String.format(
                "You have already set game class signs for team %s. - You can add more signs for: %s, however this is optional, " +
                        "because it is not a requirement to give the players access to all classes.",
                teamType.displayName(),
                StringUtils.join(all, ", ")
            )
        );
    }

    @Override
    public RuleState validate(DataContainer dataContainer) {
        Arena arena = dataContainer.get("arena");
        TeamType teamType = dataContainer.get("team");

        int signs = arena.getGameClassesSigns(teamType, true).size();

        if(signs == GameClasses.values().length) {
            return RuleState.FULFILLED;
        } else if(signs == 0) {
            return RuleState.ERROR;
        }

        return RuleState.ACCEPTABLE;

    }

}
