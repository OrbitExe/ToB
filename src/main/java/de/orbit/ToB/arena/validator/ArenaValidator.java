package de.orbit.ToB.arena.validator;

import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.team.TeamTypes;
import de.orbit.ToB.arena.validator.rules.*;
import org.spongepowered.api.text.Text;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ArenaValidator {

    private Map<Rule, DataContainer> rules = new LinkedHashMap<>();

    public ArenaValidator(Arena arena) {

        DataContainer defaultContainer = new DataContainer();
        defaultContainer.add("arena", arena);

        DataContainer redContainer = new DataContainer();
        redContainer.add("arena", arena);
        redContainer.add("team", TeamTypes.RED);

        DataContainer blueContainer = new DataContainer();
        blueContainer.add("arena", arena);
        blueContainer.add("team", TeamTypes.BLUE);

        //--- Min Player Value Rule
        this.rules.put(new MinPlayerValueRule(), defaultContainer);

        //--- Player Dividable Rule
        this.rules.put(new PlayerDividableRule(), defaultContainer);

        //--- Pressure Plate Rule
        this.rules.put(new PressurePlateRule(), redContainer);
        this.rules.put(new PressurePlateRule(), blueContainer);

        //--- Button Rule
        this.rules.put(new ButtonRule(), redContainer);
        this.rules.put(new ButtonRule(), blueContainer);

        //--- Game Classes Rule
        this.rules.put(new GameClassesRule(), redContainer);
        this.rules.put(new GameClassesRule(), blueContainer);

        //--- Spawn Point Rule
        this.rules.put(new SpawnPointRule(), redContainer);
        this.rules.put(new SpawnPointRule(), blueContainer);

    }

    public List<ArenaValidatorEntry> validate() {

        List<ArenaValidatorEntry> results = new LinkedList<>();

        this.rules.forEach((r, d) -> results.add(new ArenaValidatorEntry(
            r.displayName(d),
            r.description(d),
            r.validate(d)
        )));

        return results;

    }

    public class ArenaValidatorEntry {

        private Text displayName;
        private Text description;

        private boolean isValid;

        public ArenaValidatorEntry(Text displayName, Text description, boolean isValid) {
            this.displayName = displayName;
            this.description = description;
            this.isValid = isValid;
        }

        public Text displayName() {
            return this.displayName;
        }

        public Text getDescription() {
            return this.description;
        }

        public boolean isValid() {
            return this.isValid;
        }

    }

}
