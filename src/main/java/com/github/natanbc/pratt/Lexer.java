package com.github.natanbc.pratt;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.PushbackReader;
import java.io.Reader;
/**
 * Transforms the data in a reader into a stream of tokens.
 */
public abstract class Lexer extends CharacterStream {
    private Token nextToken;
    
    public Lexer(@Nonnull Reader reader) {
        super(reader);
    }
    
    @Deprecated
    public Lexer(@Nonnull PushbackReader reader) {
        this((Reader)reader);
    }
    
    /**
     * Parses the next token in the source. If the end is reached, this method
     * <b>must</b> return a token with the {@link #eofKind() EOF kind}.
     *
     * @return The next token.
     */
    @Nonnull
    @CheckReturnValue
    public abstract Token parse();
    
    /**
     * Returns the object representing the EOF token kind. All
     * objects returned by this method should be {@link Object#equals(Object) equal}.
     *
     * @return The object representing the EOF token kind.
     */
    @Nonnull
    @CheckReturnValue
    public abstract TokenKind eofKind();
    
    /**
     * Returns the next token available. If no more tokens are available,
     * an EOF token is returned.
     *
     * @return The next token available.
     */
    @Nonnull
    @CheckReturnValue
    public Token next() {
        if(nextToken != null) {
            Token t = nextToken;
            nextToken = null;
            return t;
        }
        return parse();
    }
    
    /**
     * Skips the next token, so that it isn't returned by {@link #next()}.
     */
    public void skip() {
        if(nextToken != null) {
            nextToken = null;
            return;
        }
        //noinspection ResultOfMethodCallIgnored
        parse();
    }
    
    /**
     * Pushes a token to the history. This token will be returned
     * by the next call to {@link #next()}. The history can only hold
     * a single token, and this method will throw if there's already
     * a token stored.
     *
     * @param t Token to store.
     */
    public void push(@Nonnull Token t) {
        if(nextToken != null) {
            throw new IllegalStateException("Next token already set!");
        }
        nextToken = t;
    }
    
    /**
     * Returns a string containing a {@link #context(Position, int, int) context}
     * for the given token, with the token value highlighted. This method is
     * useful for generating helpful error messages, with context around the
     * token so it can be located easier.
     *
     * <br>{@link #next()} should not be called after calling this method, as
     * reading the context may result in an inconsistent state.
     *
     * @param token Token to highlight.
     *
     * @return The context with the token highlighted.
     */
    @Nonnull
    @CheckReturnValue
    public String prettyContextFor(@Nonnull Token token) {
        return prettyContextFor(token, 5);
    }
    
    /**
     * Returns a string containing a {@link #context(Position, int, int) context}
     * for the given token, with the token value highlighted. This method is
     * useful for generating helpful error messages, with context around the
     * token so it can be located easier.
     *
     * <br>{@link #next()} should not be called after calling this method, as
     * reading the context may result in an inconsistent state.
     *
     * @param token Token to highlight.
     * @param around How many characters to include before and after the token, at most.
     *
     * @return The context with the token highlighted.
     */
    @Nonnull
    @CheckReturnValue
    public String prettyContextFor(@Nonnull Token token, @Nonnegative int around) {
        return prettyContext(token.position(), token.value().length(), around);
    }
    
    /**
     * Returns a {@link #context(Position, int, int) context} for the given token.
     * This method is useful for generating helpful error messages, with context
     * around the token so it can be located easier.
     *
     * <br>{@link #next()} should not be called after calling this method, as
     * reading the context may result in an inconsistent state.
     *
     * @param token Token for the context.
     *
     * @return The context.
     */
    @Nonnull
    @CheckReturnValue
    public ErrorContext contextFor(@Nonnull Token token) {
        return contextFor(token, 5);
    }
    
    /**
     * Returns a {@link #context(Position, int, int) context} for the given token.
     * This method is useful for generating helpful error messages, with context
     * around the token so it can be located easier.
     *
     * <br>{@link #next()} should not be called after calling this method, as
     * reading the context may result in an inconsistent state.
     *
     * @param token Token for the context.
     * @param around How many characters to include before and after the token, at most.
     *
     * @return The context.
     */
    @Nonnull
    @CheckReturnValue
    public ErrorContext contextFor(@Nonnull Token token, @Nonnegative int around) {
        return context(token.position(), token.value().length(), around);
    }
}
