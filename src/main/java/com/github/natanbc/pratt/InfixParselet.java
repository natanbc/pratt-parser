package com.github.natanbc.pratt;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Parses an infix operator, eg the {@code +} in {@code 1 + 2}.
 *
 * @param <C> Type of the object used for passing state for the parser.
 * @param <R> Result type of the parser.
 */
public interface InfixParselet<C, R> {
    /**
     * The precedence of this operator. Higher precedence operators are executed first.
     *
     * <br>For example, in math {@code *} has a higher precedence than {@code +}.
     *
     * @return This operator's precedence.
     */
    @Nonnegative
    @CheckReturnValue
    int precedence();
    
    /**
     * Parses the operator. To read additional values, call {@link Parser#parseExpression(Object, int)}.
     * Calling it with the same {@link #precedence() precedence} as this operator makes it left associative,
     * a smaller value makes it right associative.
     *
     * @param context State of the parser.
     * @param parser Parser responsible for the expression.
     * @param left Value on the left of this parser.
     * @param token Token that triggered this parser.
     *
     * @return A value representing this operator applied to the provided {@code left} value
     *         and additional values parsed.
     */
    @Nonnull
    @CheckReturnValue
    R parse(C context, @Nonnull Parser<C, R> parser, @Nonnull R left, @Nonnull Token token);
}
