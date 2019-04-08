package com.github.natanbc.pratt;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Transforms the data in a reader into a stream of tokens.
 */
public abstract class Lexer {
    private final Map<Integer, StringBuilder> lineMap = new HashMap<>();
    private final CharHistory unreadChars = new CharHistory(0);
    private final Reader reader;
    private int line = 1;
    private int column = 0;
    private Token nextToken;
    private int lastLine = -1;
    private int lastColumn = -1;
    
    public Lexer(@Nonnull Reader reader) {
        this.reader = reader;
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
    protected abstract Token parse();
    
    /**
     * Returns the object representing the EOF token kind. All
     * objects returned by this method should be {@link Object#equals(Object) equal}.
     *
     * @return The object representing the EOF token kind.
     */
    @Nonnull
    @CheckReturnValue
    protected abstract TokenKind eofKind();
    
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
     * Returns a string containing a {@link #context(Position, int, int) context}
     * for the given position, with {@code length} characters highlighted. This method is
     * useful for generating helpful error messages, with context around the
     * token so it can be located easier.
     *
     * <br>{@link #next()} should not be called after calling this method, as
     * reading the context may result in an inconsistent state.
     *
     * @param pos Position to start highlighting.
     * @param length How many characters to highlight.
     *
     * @return The context with the token highlighted.
     */
    @Nonnull
    @CheckReturnValue
    public String prettyContext(@Nonnull Position pos, @Nonnegative int length) {
        return prettyContext(pos, length, 5);
    }
    
    /**
     * Returns a string containing a {@link #context(Position, int, int) context}
     * for the given position, with {@code length} characters highlighted. This method is
     * useful for generating helpful error messages, with context around the
     * token so it can be located easier.
     *
     * <br>{@link #next()} should not be called after calling this method, as
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
    public String prettyContext(@Nonnull Position pos, @Nonnegative int length, @Nonnegative int around) {
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
    
    /**
     * Returns a {@link #context(Position, int, int) context} for the given position.
     * This method is useful for generating helpful error messages, with context
     * around the position so it can be located easier.
     *
     * <br>{@link #next()} should not be called after calling this method, as
     * reading the context may result in an inconsistent state.
     *
     * @param pos Position for the context.
     * @param length Length of the token.
     *
     * @return The context.
     */
    @Nonnull
    @CheckReturnValue
    public ErrorContext context(@Nonnull Position pos, @Nonnegative int length) {
        return context(pos, length, 5);
    }
    
    /**
     * Returns a context for the given position.
     * This method is useful for generating helpful error messages, with context
     * around the position so it can be located easier.
     *
     * <br>{@link #next()} should not be called after calling this method, as
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
    public ErrorContext context(@Nonnull Position pos, @Nonnegative int length, @Nonnegative int around) {
        if(pos.line() == line) {
            int l = line;
            int remaining = length + around;
            //read until next line or EOF (to ensure there are enough characters after the token)
            while(line == l && remaining > 0) {
                if(read(true) == -1) break;
                remaining--;
            }
        }
        StringBuilder buffer = lineBuffer(pos.line());
        int before = Math.min(pos.column() - 1, around);
        int after = Math.min(buffer.length() - (pos.column() + length), around);
        int max = buffer.charAt(buffer.length() - 1) == '\n' ? buffer.length() - 1 : buffer.length();
        String str = buffer.substring(Math.max(pos.column() - around - 1, 0),
                Math.min(pos.column() + around + length - 1, max));
        return new ErrorContext(before, after, str);
    }
    
    /**
     * Returns an object representing the current position in the source.
     *
     * @return The current position.
     */
    @Nonnull
    @CheckReturnValue
    protected Position pos() {
        return new Position(line, column);
    }
    
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
    protected int peek(boolean ignoreWhitespace) {
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
    protected boolean match(char ch) {
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
    protected boolean match(char ch, boolean ignoreWhitespace) {
        return peek(ignoreWhitespace) == ch;
    }
    
    /**
     * Returns to a position in the past. The provided position must be
     * before the current one.
     *
     * @param pos Position to move to.
     */
    protected void backTo(@Nonnull Position pos) {
        backTo(pos.line(), pos.column());
    }
    
    /**
     * Returns to a position in the past. The provided position must be
     * before the current one.
     *
     * @param line Line to move to.
     * @param column Column to move to.
     */
    protected void backTo(int line, int column) {
        if(line > this.line || (line == this.line && column > this.column)) {
            throw new IllegalArgumentException("Cannot go back to a position ahead of the current!");
        }
        while(line != this.line || column < this.column) {
            char last;
            StringBuilder sb = lineBuffer();
            int len = sb.length();
            if(len > 0) {
                last = sb.charAt(len - 1);
                sb.setLength(len - 1);
            } else {
                last = '\n';
            }
            if(last == '\n') {
                this.line--;
                this.column = lineBuffer().length();
            } else {
                this.column--;
            }
            unreadChars.insert(last);
        }
    }
    
    /**
     * Returns the lexer to the state it was before the last
     * call to {@link #read(boolean)}. Only one call can be
     * undone by this method.
     *
     * <br>To undo more, you should use {@link #backTo(Position)} instead.
     */
    protected void back() {
        if(lastLine == -1) {
            throw new IllegalStateException("Cannot go back more than one call to read()!");
        }
        backTo(lastLine, lastColumn);
        lastLine = -1;
        lastColumn = -1;
    }
    
    /**
     * Pushes a character to the buffer. This character will be returned
     * by the next call to {@link #read(boolean)}.
     *
     * @param ch Character to insert.
     *
     * @deprecated Use {@link #back()} or {@link #backTo(Position)} instead.
     */
    @Deprecated
    protected void unread(@Nonnegative int ch) {
        StringBuilder sb = lineBuffer();
        int len = sb.length();
        if(len > 0) {
            sb.setLength(len - 1);
        }
        if(ch == '\n') {
            this.line--;
            this.column = lineBuffer().length();
        } else {
            column -= 1;
        }
        unreadChars.insert((char)ch);
    }
    
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
    protected int read(boolean ignoreWhitespace) {
        lastLine = line;
        lastColumn = column;
        try {
            while(true) {
                int c = unreadChars.size() > 0 ? unreadChars.remove() : reader.read();
                if(c == -1) {
                    return -1;
                }
                char ch = (char)c;
                lineBuffer().append(ch);
                if(ch == '\n') {
                    line++;
                    column = 0;
                    if(!ignoreWhitespace) {
                        return ch;
                    }
                } else {
                    column++;
                    if(ignoreWhitespace && Character.isWhitespace(ch)) {
                        continue;
                    }
                    return ch;
                }
            }
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    /**
     * Returns the buffer containing the characters read so far from the current line.
     *
     * <br>This object is managed by the lexer, so it should not be modified.
     *
     * @return The buffer containing the current line so far.
     */
    @Nonnull
    @CheckReturnValue
    protected StringBuilder lineBuffer() {
        return lineBuffer(line);
    }
    
    /**
     * Returns the buffer containing the characters read so far from the provided line.
     *
     * <br>This object is managed by the lexer, so it should not be modified.
     *
     * @param line Line wanted.
     *
     * @return The buffer containing the provided line so far.
     */
    @Nonnull
    @CheckReturnValue
    protected StringBuilder lineBuffer(int line) {
        return lineMap.computeIfAbsent(line, __ -> new StringBuilder());
    }
}
