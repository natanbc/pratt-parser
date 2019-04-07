package com.github.natanbc.pratt;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

/**
 * Parses a prefix operator, such as the {@code -} in {@code -10}.
 *
 * @param <C> Type of the object used for passing state for the parser.
 * @param <R> Result type of the parser.
 */
public interface PrefixParselet<C, R> {
    /**
     * Parses the operator. To read additional values, call {@link Parser#parseExpression(Object, int)}.
     *
     * @param context State of the parser.
     * @param parser Parser responsible for the expression.
     * @param token Token that triggered this parser.
     *
     * @return A value representing this operator, possibly applied to
     *         additional values parsed by it.
     */
    @Nonnull
    @CheckReturnValue
    R parse(C context, @Nonnull Parser<C, R> parser, @Nonnull Token token);
}
