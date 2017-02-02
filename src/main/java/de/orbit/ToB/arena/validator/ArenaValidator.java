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
    private List<ArenaValidatorEntry> results = new LinkedList<>();


    public ArenaValidator(Arena arena) {

        DataContainer defaultContainer = new DataContainer();
        defaultContainer.add("arena", arena);

        DataContainer redContainer = new DataContainer();
        redContainer.add("arena", arena);
        redContainer.add("team", TeamTypes.RED);

        DataContainer blueContainer = new DataContainer();
        blueContainer.add("arena", arena);
        blueContainer.add("team", TeamTypes.BLUE);

        //--- Player Dividable Rule
        this.rules.put(new PlayerDividableRule(), defaultContainer);

        //--- Min Player Value Rule
        this.rules.put(new MinPlayerValueRule(), defaultContainer);

        //--- Tower Dimension Rule
        this.rules.put(new TowerDimensionRule(), defaultContainer);

        //--- Overlapping Rule
        this.rules.put(new OverlappingRule(), defaultContainer);

        //--- Lobby Sign Rule
        this.rules.put(new LobbySignRule(), defaultContainer);

        //--- Sand Volume Rule
        this.rules.put(new SandRule(), defaultContainer);

        //--- Same World Rule
        this.rules.put(new SameWorldRule(), defaultContainer);

        //--- Red
        this.rules.put(new PressurePlateRule(), redContainer);
        this.rules.put(new GameClassesRule(), redContainer);
        this.rules.put(new SpawnPointRule(), redContainer);
        this.rules.put(new ButtonRule(), redContainer);


        //--- Blue
        this.rules.put(new PressurePlateRule(), blueContainer);
        this.rules.put(new GameClassesRule(), blueContainer);
        this.rules.put(new SpawnPointRule(), blueContainer);
        this.rules.put(new ButtonRule(), blueContainer);

    }

    /**
     * <p>
     *    Checks if the arena is valid.
     * </p>
     *
     * @return
     */
    public boolean isValid() {
        return this.count(RuleState.ERROR) == 0;
    }

    /**
     * <p>
     *    Count how many results are in the provided {@link RuleState}.
     * </p>
     *
     * @param ruleState Counts in the result state, if null it returns all combined together.
     * @return
     */
    public long count(RuleState ruleState) {

        if(!(ruleState == null)) {
            return this.results.stream().filter(e -> e.getRuleState() == ruleState).count();
        }

        return this.results.size();

    }

    /**
     * <p>
     *      This method calls {@link Rule#validate(DataContainer)} and related methods to verify the rules.
     * </p>
     *
     * @return
     */
    public List<ArenaValidatorEntry> validate() {

        this.results.clear();

        this.rules.forEach((r, d) -> this.results.add(new ArenaValidatorEntry(
            r.validate(d),
            r.displayName(d),
            r.description(d)
        )));

        return this.results;

    }

    /**
     * <p>
     *    This is just a wrapper object. This makes it easier to store results.
     * </p>
     */
    public class ArenaValidatorEntry {

        private Text displayName;
        private Text description;

        private RuleState ruleState;

        public ArenaValidatorEntry(RuleState ruleState, Text displayName, Text description) {
            this.displayName = displayName;
            this.description = description;
            this.ruleState = ruleState;
        }

        public Text displayName() {
            return this.displayName;
        }

        public Text getDescription() {
            return this.description;
        }

        public RuleState getRuleState() {
            return this.ruleState;
        }

    }

}
