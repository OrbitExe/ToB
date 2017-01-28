package de.orbit.ToB.arena.validator;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public enum RuleState {

    /**
     * If a rule is fulfilled, it is in the most optimal way it can be.
     */
    FULFILLED(TextColors.GREEN, '✔'),

    /**
     * If a rule is acceptable, it works, but if more work is applied it might improve the quality.
     */
    ACCEPTABLE(TextColors.YELLOW, '-'),

    /**
     * If the rule is resulting in an error, it is not fulfilled at all and stops the game from working.
     */
    ERROR(TextColors.DARK_RED, '✘');

    private TextColor textColor;
    private char c;

    RuleState(TextColor textColor, char c) {
        this.textColor = textColor;
        this.c = c;
    }

    public char c() {
        return this.c;
    }

    public TextColor color() {
        return this.textColor;
    }
}
