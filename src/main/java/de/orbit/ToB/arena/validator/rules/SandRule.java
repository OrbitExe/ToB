package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.ToBMath;
import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
import de.orbit.ToB.arena.validator.RuleState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.extent.Extent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SandRule implements Rule {

    private Map<Integer, CacheEntry> cache = new HashMap<>();

    @Override
    public Text displayName(DataContainer dataContainer) {
        return Text.of("Sand Volume");
    }

    @Override
    public Text description(DataContainer dataContainer) {

        Arena arena = dataContainer.get("arena");

        if(arena.getAreaMin() == null || arena.getAreaMax() == null || arena.getTowerDimension() == null) {
            return Text.of(
                "You need to set the min & max boundaries of the arena as well as the tower dimension before this rule " +
                        "can be applied."
            );
        }

        assert this.cache.containsKey(arena.getIdentifier());

        CacheEntry entry = this.cache.get(arena.getIdentifier());

        if(entry.getSum() < entry.getRequired()) {
            return Text.of(String.format(
                "You haven't enough sand placed in the arena %d for the towers to be buildable. You only have %d, but you " +
                        "need at least %d sand blocks.",
                    arena.getIdentifier(),
                    entry.getSum(),
                    entry.getRequired()
            ));
        } else if(entry.getSum() >= entry.getRequired() && entry.getSum() <= entry.getRequired() * 0.05D) {
            return Text.of(String.format(
                "You have enough blocks placed, but this might not be not enough to be fun. The applied error margin is 5%%. " +
                        "- You currently have %d out of %d required blocks in the arena %d.",
                    entry.getSum(),
                    entry.getRequired(),
                    arena.getIdentifier()
            ));
        }

        return Text.of(String.format(
            "You have set enough blocks that you can expect a stable game balance. The arena %d currently contains " +
                    "%d out of %d required blocks.",
                arena.getIdentifier(),
                entry.getSum(),
                entry.getRequired()
        ));
    }

    @Override
    public RuleState validate(DataContainer dataContainer) {

        Arena arena = dataContainer.get("arena");

        if(arena.getAreaMin() == null || arena.getAreaMax() == null || arena.getTowerDimension() == null) {
            return RuleState.ERROR;
        }

        //--- create the view
        Extent view = arena.getAreaMin().getExtent().getExtentView(
            arena.getAreaMin().getBlockPosition(), arena.getAreaMax().getBlockPosition()
        );

        //--- Count sand
        int sum = view.getBlockWorker(Cause.of(NamedCause.of("rule", this)))
                    .reduce(
                        (volume, x, y, z, reduction) -> (volume.getBlockType(x, y, z).equals(BlockTypes.SAND) ? reduction + 1 : reduction),
                        (a, b) -> (a + b),
                        0
                    );

        Dimension towerDimension = arena.getTowerDimension();
        int required = ToBMath.summation(1, (int) (towerDimension.getWidth() * towerDimension.getHeight())) * 2;

        this.cache.put(arena.getIdentifier(), new CacheEntry(sum, required));

        if(sum < required) {
            return RuleState.ERROR;
        } else if(sum >= required && sum <= required * 0.05D) {
            return RuleState.ACCEPTABLE;
        }

        return RuleState.FULFILLED;
    }

    /**
     * <p>
     *    A CacheEntry contains the sum & the required amount of sand for an arena, because the operation to figure
     *    these values out is quiet expensive.
     * </p>
     */
    private class CacheEntry {

        private int sum;
        private int required;

        public CacheEntry(int sum, int required) {
            this.sum = sum;
            this.required = required;
        }

        public int getSum() {
            return this.sum;
        }

        public int getRequired() {
            return this.required;
        }

    }

}
