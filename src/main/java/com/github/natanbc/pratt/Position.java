package com.github.natanbc.pratt;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;

/**
 * Represents a position in the source, composed of line and column numbers.
 */
public class Position {
    private final int line;
    private final int column;
    
    public Position(@Nonnegative int line, @Nonnegative int column) {
        this.line = line;
        this.column = column;
    }
    
    /**
     * @return The line of this position. 1 based.
     */
    @Nonnegative
    @CheckReturnValue
    public int line() {
        return line;
    }
    
    /**
     * @return The column of this position. 1 based.
     */
    @Nonnegative
    @CheckReturnValue
    public int column() {
        return column;
    }
    
    @Override
    public int hashCode() {
        return line << 16 | column;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Position)) {
            return false;
        }
        Position p = (Position) obj;
        return p.line == line && p.column == column;
    }
    
    @Override
    public String toString() {
        return "Position(line = " + line + ", column = " + column + ")";
    }
}

