package com.github.natanbc.pratt;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public interface CharacterStream {
    /**
     * Returns a string containing a {@link #context(Position, int, int) context}
     * for the given position, with {@code length} characters highlighted. This method is
     * useful for generating helpful error messages, with context around the
     * token so it can be located easier.
     *
     * <br>{@link #read(boolean)} should not be called after calling this method, as
     * reading the context may result in an inconsistent state.
     *
     * @param pos Position to start highlighting.
     * @param length How many characters to highlight.
     *
     * @return The context with the token highlighted.
     */
    @Nonnull
    @CheckReturnValue
    default String prettyContext(@Nonnull Position pos, @Nonnegative int length) {
        return prettyContext(pos, length, 5);
    }
    
    /**
     * Returns a string containing a {@link #context(Position, int, int) context}
     * for the given position, with {@code length} characters highlighted. This method is
     * useful for generating helpful error messages, with context around the
     * token so it can be located easier.
     *
     * <br>{@link #read(boolean)} should not be called after calling this method, as
     * reading the context may result in an inconsistent state.
     *
     * @param pos Position to start highlighting.
     * @param length How many characters to highlight.
     * @param around How many characters to include before and after the token, at most.
     *
     * @return The context with the token highlighted.
     */
    @Nonnull
    @CheckReturnValue
    default String prettyContext(@Nonnull Position pos, @Nonnegative int length, @Nonnegative int around) {
        ErrorContext context = context(pos, length, around);
        StringBuilder sb = new StringBuilder(context.value())
                .append('\n');
        for(int i = 0; i < context.charsBefore(); i++) {
            sb.append(' ');
        }
        if(length > 1) {
            sb.append('└');
            for(int i = 2; i < length; i++) {
                sb.append('─');
            }
            sb.append('┘');
        } else {
            sb.append('^');
        }
        return sb.toString();
    }
    
    /**
     * Returns a {@link #context(Position, int, int) context} for the given position.
     * This method is useful for generating helpful error messages, with context
     * around the position so it can be located easier.
     *
     * <br>{@link #read(boolean)} should not be called after calling this method, as
     * reading the context may result in an inconsistent state.
     *
     * @param pos Position for the context.
     * @param length Length of the token.
     *
     * @return The context.
     */
    @Nonnull
    @CheckReturnValue
    default ErrorContext context(@Nonnull Position pos, @Nonnegative int length) {
        return context(pos, length, 5);
    }
    
    /**
     * Returns a context for the given position.
     * This method is useful for generating helpful error messages, with context
     * around the position so it can be located easier.
     *
     * <br>{@link #read(boolean)} should not be called after calling this method, as
     * reading the context may result in an inconsistent state.
     *
     * @param pos Position for the context.
     * @param length Length of the token.
     * @param around How many characters to include before and after the token, at most.
     *
     * @return The context.
     */
    @Nonnull
    @CheckReturnValue
    ErrorContext context(@Nonnull Position pos, @Nonnegative int length, @Nonnegative int around);
    
    /**
     * Returns an object representing the current position in the source.
     *
     * @return The current position.
     */
    @Nonnull
    @CheckReturnValue
    Position pos();
    
    /**
     * Returns the next character in the stream, inserting it
     * back so it can be read again.
     *
     * @param ignoreWhitespace Whether or not whitespace characters should be ignored.
     *                         If they aren't ignored, the first non whitespace character
     *                         is returned, or -1 if the stream finishes before.
     *
     * @return The next character in the stream.
     */
    @CheckReturnValue
    default int peek(boolean ignoreWhitespace) {
        int i = read(ignoreWhitespace);
        if(i != -1) {
            back();
        }
        return i;
    }
    
    /**
     * Returns whether or not the next character matches the
     * provided one.
     *
     * <br>Equivalent to {@code match(ch, false)}.
     *
     * @param ch Character to match.
     *
     * @return Whether or not the next character matches the provided one.
     */
    default boolean match(char ch) {
        return match(ch, false);
    }
    
    /**
     * Returns whether or not the next character matches the
     * provided one.
     *
     * @param ch Character to match.
     * @param ignoreWhitespace Whether or not whitespace should be ignored.
     *
     * @return Whether or not the next character matches the provided one.
     */
    default boolean match(char ch, boolean ignoreWhitespace) {
        return peek(ignoreWhitespace) == ch;
    }
    
    /**
     * Returns to a position in the past. The provided position must be
     * before the current one.
     *
     * @param pos Position to move to.
     */
    default void backTo(@Nonnull Position pos) {
        backTo(pos.line(), pos.column());
    }
    
    /**
     * Returns to a position in the past. The provided position must be
     * before the current one.
     *
     * @param line Line to move to.
     * @param column Column to move to.
     */
    void backTo(int line, int column);
    
    /**
     * Returns the lexer to the state it was before the last
     * call to {@link #read(boolean)}. Only one call can be
     * undone by this method.
     *
     * <br>To undo more, you should use {@link #backTo(Position)} instead.
     */
    void back();
    
    /**
     * Pushes a character to the buffer. This character will be returned
     * by the next call to {@link #read(boolean)}.
     *
     * @param ch Character to insert.
     *
     * @deprecated Use {@link #back()} or {@link #backTo(Position)} instead.
     */
    @Deprecated
    void unread(@Nonnegative int ch);
    
    /**
     * Returns the next character in the stream.
     *
     * @param ignoreWhitespace Whether or not whitespace characters should be ignored.
     *                         If they aren't ignored, the first non whitespace character
     *                         is returned, or -1 if the stream finishes before.
     *
     * @return The next character in the stream.
     */
    @CheckReturnValue
    int read(boolean ignoreWhitespace);
}
