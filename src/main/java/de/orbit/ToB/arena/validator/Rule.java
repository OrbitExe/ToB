package de.orbit.ToB.arena.validator;

import org.spongepowered.api.text.Text;

public interface Rule {

    /**
     * <p>
     *    Gives a human-readable text as short description or display name of the rule.
     * </p>
     *
     * @param dataContainer
     * @return
     */
    Text displayName(DataContainer dataContainer);

    /**
     * <p>
     *    A longer description of what the rule expects you to do.
     * </p>
     *
     * @param dataContainer
     * @return
     */
    Text description(DataContainer dataContainer);

    /**
     * <p>
     *    This method will be invoked by the validator to check in what state the required rule is.
     * </p>
     *
     * @param dataContainer
     * @return
     */
    RuleState validate(DataContainer dataContainer);

}
