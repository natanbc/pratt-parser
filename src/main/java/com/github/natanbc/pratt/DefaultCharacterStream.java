package com.github.natanbc.pratt;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class DefaultCharacterStream implements CharacterStream {
    protected final Map<Integer, StringBuilder> lineMap = new HashMap<>();
    protected final CharHistory unreadChars = new CharHistory(0);
    protected final Reader reader;
    protected int line = 1;
    protected int column = 0;
    protected int lastLine = -1;
    protected int lastColumn = -1;
    
    public DefaultCharacterStream(Reader reader) {
        this.reader = reader;
    }
    
    @Nonnull
    @CheckReturnValue
    @Override
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
    
    @Nonnull
    @CheckReturnValue
    @Override
    public Position pos() {
        return new Position(line, column);
    }
    
    @CheckReturnValue
    @Override
    public int peek(boolean ignoreWhitespace) {
        int i = read(ignoreWhitespace);
        if(i != -1) {
            back();
        }
        return i;
    }
    
    @Override
    public boolean match(char ch) {
        return match(ch, false);
    }
    
    @Override
    public boolean match(char ch, boolean ignoreWhitespace) {
        return peek(ignoreWhitespace) == ch;
    }
    
    @Override
    public void backTo(@Nonnull Position pos) {
        backTo(pos.line(), pos.column());
    }
    
    @Override
    public void backTo(int line, int column) {
        if(line > this.line || (line == this.line && column > this.column)) {
            throw new IllegalArgumentException("Cannot go back to a position ahead of the current!");
        }
        while(line < this.line || column < this.column) {
            char last;
            StringBuilder sb = lineBuffer();
            int len = sb.length();
            if(len == 0) {
                sb = lineBuffer(this.line - 1);
                len = sb.length();
            }
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
    
    @Override
    public void back() {
        if(lastLine == -1) {
            throw new IllegalStateException("Cannot go back more than one call to read()!");
        }
        backTo(lastLine, lastColumn);
        lastLine = -1;
        lastColumn = -1;
    }
    
    @Deprecated
    @Override
    public void unread(@Nonnegative int ch) {
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
    
    @CheckReturnValue
    @Override
    public int read(boolean ignoreWhitespace) {
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
    public StringBuilder lineBuffer() {
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
    public StringBuilder lineBuffer(int line) {
        return lineMap.computeIfAbsent(line, __ -> new StringBuilder());
    }
}
