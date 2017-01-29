package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.ArenaPlateEntry;
import de.orbit.ToB.arena.ArenaSignEntry;
import de.orbit.ToB.arena.team.TeamTypes;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
import de.orbit.ToB.arena.validator.RuleState;
import de.orbit.ToB.classes.GameClass;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;

public class SameWorldRule implements Rule {

    @Override
    public Text displayName(DataContainer dataContainer) {
        return Text.of("Same World");
    }

    @Override
    public Text description(DataContainer dataContainer) {

        RuleState ruleState = this.validate(dataContainer);

        switch (ruleState) {

            case FULFILLED:
                return Text.of("All components (spawn points, buttons, game class signs and pressure plates) are correctly" +
                        " placed in the same world.");

            case ACCEPTABLE:
                return Text.of("You haven't placed any components (spawn points, buttons, game class signs and pressure plates) " +
                        "yet. The rule can only be applied if there is at least one component placed.");

            case ERROR:
                return Text.of("You have placed some or all components (spawn points, buttons, game class signs and pressure plates) " +
                        "outside of the same world. Please make sure that spawn points, buttons, game class signs and " +
                        "pressure plates are all in the same world. - Or you haven't set all component locations yet.");

        }

        throw new IllegalArgumentException();

    }

    @Override
    public RuleState validate(DataContainer dataContainer) {

        Arena arena = dataContainer.get("arena");

        //--- Spawn Points
        Location<World> blueSpawn = arena.getSpawnPoint(TeamTypes.BLUE);
        Location<World> redSpawn = arena.getSpawnPoint(TeamTypes.RED);

        //--- Game Class Signs
        List<ArenaSignEntry> blueSigns = arena.getGameClassesSigns(TeamTypes.BLUE, false);
        List<ArenaSignEntry> redSigns = arena.getGameClassesSigns(TeamTypes.RED, false);

        //--- Buttons
        Location<World> blueButton = arena.getButton(TeamTypes.BLUE);
        Location<World> redButton = arena.getButton(TeamTypes.RED);

        //--- Pressure Plates
        List<ArenaPlateEntry> bluePlates = arena.getPlates(TeamTypes.BLUE);
        List<ArenaPlateEntry> redPlates = arena.getPlates(TeamTypes.RED);

        //--- Bounds
        Location<World> arenaMin = arena.getAreaMin();
        Location<World> arenaMax = arena.getAreaMax();

        // Find a location that is not null to use it to compare
        World comparison = this.findWorld(blueSpawn, redSpawn, blueSigns, redSigns, blueButton, redButton, bluePlates, redPlates, arenaMin, arenaMax);

        if(!(comparison == null)) {

            //--- Spawn Points
            if(blueSpawn == null || !(blueSpawn.getExtent().equals(comparison))) {
                return RuleState.ERROR;
            }

            if(redSpawn == null || !(redSpawn.getExtent().equals(comparison))) {
                return RuleState.ERROR;
            }

            //--- Game Class Signs
            for(ArenaSignEntry e : blueSigns) {
                if(!(e.getSign().getLocation().getExtent().equals(comparison))) {
                    return RuleState.ERROR;
                }
            }

            if(blueSigns.isEmpty()) {
                return RuleState.ERROR;
            }

            for(ArenaSignEntry e : redSigns) {
                if(!(e.getSign().getLocation().getExtent().equals(comparison))) {
                    return RuleState.ERROR;
                }
            }

            if(redSigns.isEmpty()) {
                return RuleState.ERROR;
            }

            //--- Buttons
            if(blueButton == null || !(blueButton.getExtent().equals(comparison))) {
                return RuleState.ERROR;
            }

            if(redButton == null || !(redButton.getExtent().equals(comparison))) {
                return RuleState.ERROR;
            }

            //--- Pressure Plates
            for(ArenaPlateEntry e : bluePlates) {
                if(!(e.getLocation().getExtent().equals(comparison))) {
                    return RuleState.ERROR;
                }
            }

            if(bluePlates.isEmpty()) {
                return RuleState.ERROR;
            }

            for(ArenaPlateEntry e : redPlates) {
                if(!(e.getLocation().getExtent().equals(comparison))) {
                    return RuleState.ERROR;
                }
            }

            if(redPlates.isEmpty()) {
                return RuleState.ERROR;
            }

            //--- Bounds
            if(arenaMin == null || !(arenaMin.getExtent().equals(comparison))) {
                return RuleState.ERROR;
            }

            if(arenaMax == null || !(arenaMax.getExtent().equals(comparison))) {
                return RuleState.ERROR;
            }

            return RuleState.FULFILLED;

        }

        return RuleState.ACCEPTABLE;

    }

    /**
     * <p>
     *    We have to check all values to find a value to compare it agains.
     * </p>
     *
     * @param blueSpawn
     * @param redSpawn
     * @param blueSigns
     * @param redSigns
     * @param blueButton
     * @param redButton
     * @param bluePlates
     * @param redPlates
     * @return
     */
    private World findWorld(
            Location<World> blueSpawn, Location<World> redSpawn,
            List<ArenaSignEntry> blueSigns, List<ArenaSignEntry> redSigns,
            Location<World> blueButton, Location<World> redButton,
            List<ArenaPlateEntry> bluePlates, List<ArenaPlateEntry> redPlates,
            Location<World> areaMin, Location<World> areaMax
    ) {

        if(!(blueSpawn == null)) {
            return blueSpawn.getExtent();
        }

        if(!(redSpawn == null)) {
            return redSpawn.getExtent();
        }

        if(!(blueButton == null)) {
            return blueButton.getExtent();
        }

        if(!(redButton == null)) {
            return redButton.getExtent();
        }

        if(!(areaMin == null)) {
            return areaMin.getExtent();
        }

        if(!(areaMax == null)) {
            return areaMax.getExtent();
        }

        if(!(blueSigns.isEmpty())) {
            return blueSigns.get(0).getSign().getLocation().getExtent();
        }

        if(!(redSigns.isEmpty())) {
            return redSigns.get(0).getSign().getLocation().getExtent();
        }

        if(!(bluePlates.isEmpty())) {
            return bluePlates.get(0).getLocation().getExtent();
        }

        if(!(redPlates.isEmpty())) {
            return redPlates.get(0).getLocation().getExtent();
        }

        return null;

    }

}
