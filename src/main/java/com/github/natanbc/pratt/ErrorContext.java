package com.github.natanbc.pratt;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Context used for an error. Contains a value with {@link #charsBefore() charsBefore}
 * characters before the relevant token, the relevant token, {@link #charsAfter() charsAfter}
 * characters after the relevant token.
 *
 * All characters are in the same line on the input.
 */
public class ErrorContext {
    private final int charsBefore;
    private final int charsAfter;
    private final String value;
    
    public ErrorContext(@Nonnegative int charsBefore, @Nonnegative int charsAfter, @Nonnull String value) {
        this.charsBefore = charsBefore;
        this.charsAfter = charsAfter;
        this.value = value;
    }
    
    /**
     * @return How many characters appear before the relevant token in the {@link #value() value}.
     */
    @Nonnegative
    @CheckReturnValue
    public int charsBefore() {
        return charsBefore;
    }
    
    /**
     * @return How many characters appear after the relevant token in the {@link #value() value}.
     */
    @Nonnegative
    @CheckReturnValue
    public int charsAfter() {
        return charsAfter;
    }
    
    /**
     * @return The text around (and containing) the relevant token.
     */
    @Nonnull
    @CheckReturnValue
    public String value() {
        return value;
    }
}
