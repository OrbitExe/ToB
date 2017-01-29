package de.orbit.ToB.arena.validator.rules;

import de.orbit.ToB.arena.Arena;
import de.orbit.ToB.arena.validator.DataContainer;
import de.orbit.ToB.arena.validator.Rule;
import de.orbit.ToB.arena.validator.RuleState;
import org.spongepowered.api.text.Text;

import java.awt.*;

public class TowerDimensionRule implements Rule {

    @Override
    public Text displayName(DataContainer dataContainer) {
        return Text.of("Tower Dimension");
    }

    @Override
    public Text description(DataContainer dataContainer) {

        Arena arena = dataContainer.get("arena");

        if(arena.getTowerDimension() == null) {
            return Text.of(String.format(
                "You have to set the tower dimension for arena %d first before this rule can be applied.",
                    arena.getIdentifier()
            ));
        }

        RuleState ruleState = this.validate(dataContainer);

        switch (ruleState) {

            case ACCEPTABLE:
            case FULFILLED:
                return Text.of(String.format(
                    "You have set tower dimensions fulfilling the rule in arena %d. No more actions required.",
                        arena.getIdentifier()
                ));

            case ERROR: {

                int maxWidth = (int) Math.abs(((arena.getAreaMax().getBlockX() - arena.getAreaMin().getBlockX() / 2) * 0.5));
                int maxHeight = (int) Math.abs(((arena.getAreaMax().getBlockZ() - arena.getAreaMin().getBlockZ() / 2) * 0.5));

                return Text.of(String.format(
                        "The tower dimensions do not fulfill the rule. The min dimension is 1x1; the max dimension " +
                            "is %dx%d. The current value is %dx%d. The max height of the arena is %d the expected height " +
                                "of the tower is %d.",
                        maxWidth,
                        maxHeight,
                        (int) arena.getTowerDimension().getWidth(),
                        (int) arena.getTowerDimension().getHeight(),
                        arena.getAreaMax().getBlockPosition().getY(),
                        (maxHeight * maxWidth)
                ));
            }

        }

        throw new IllegalArgumentException();
    }

    @Override
    public RuleState validate(DataContainer dataContainer) {

        Arena arena = dataContainer.get("arena");

        if(arena.getTowerDimension() == null) {
            return RuleState.ERROR;
        }

        // x & z, because we are here in 2D space
        int maxWidth = (int) Math.abs(((arena.getAreaMax().getBlockX() - arena.getAreaMin().getBlockX() / 2) * 0.5));
        int maxHeight = (int) Math.abs(((arena.getAreaMax().getBlockZ() - arena.getAreaMin().getBlockZ() / 2) * 0.5));

        Dimension dimension = arena.getTowerDimension();

        int towerHeight = (int) (dimension.getWidth() * dimension.getHeight());

        if(
            dimension.getWidth() > maxWidth ||
            dimension.getHeight() > maxHeight ||
            dimension.getWidth() <= 0 ||
            dimension.getHeight() <= 0 ||
            arena.getAreaMax().getY() < towerHeight
        ) {
            return RuleState.ERROR;
        }

        return RuleState.FULFILLED;

    }
}
