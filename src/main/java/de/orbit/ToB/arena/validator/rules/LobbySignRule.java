package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.ArenaSignEntry;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
import de.orbit.ToB.arena.validator.RuleState;
import org.spongepowered.api.text.Text;

public class LobbySignRule implements Rule {

    @Override
    public Text displayName(DataContainer dataContainer) {
        return Text.of("Lobby Sign");
    }

    @Override
    public Text description(DataContainer dataContainer) {

       RuleState state = this.validate(dataContainer);

       switch (state) {

           case ACCEPTABLE:
           case FULFILLED:
                return Text.of(
                    "You have set one ore more lobby signs."
                );

           case ERROR:
               return Text.of(
                   "You need to set at least one lobby sign."
               );

       }

       throw new IllegalArgumentException();
    }

    @Override
    public RuleState validate(DataContainer dataContainer) {

        Arena arena = dataContainer.get("arena");

        if(arena.getSigns().stream().anyMatch(e -> e.getSignType() == ArenaSignEntry.SignType.LOBBY)) {
            return RuleState.FULFILLED;
        }

        return RuleState.ERROR;

    }

}
