package com.github.natanbc.pratt;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

/**
 * Smallest unit of the parsing. Represents any meaningful text in the source,
 * such as an operator, value, punctuation, etc.
 */
public class Token {
    private final TokenKind kind;
    private final Position position;
    private final String value;
    
    public Token(@Nonnull TokenKind kind, @Nonnull Position position, @Nonnull String value) {
        this.kind = kind;
        this.position = position;
        this.value = value;
    }
    
    /**
     * Returns the kind of this token. The {@link TokenKind} interface is just a marker type,
     * implementations are required to create instances of it.
     *
     * @return The kind of this token.
     */
    @Nonnull
    @CheckReturnValue
    public TokenKind kind() {
        return kind;
    }
    
    /**
     * @return The position of this token in the source.
     */
    @Nonnull
    @CheckReturnValue
    public Position position() {
        return position;
    }
    
    /**
     * @return A textual representation of this token.
     */
    @Nonnull
    @CheckReturnValue
    public String value() {
        return value;
    }
    
    @Override
    public int hashCode() {
        return kind.hashCode() ^ position.hashCode() ^ value.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Token)) {
            return false;
        }
        Token t = (Token) obj;
        return t.kind.equals(kind) && t.position.equals(position) && t.value.equals(value);
    }
    
    @Override
    public String toString() {
        return "Token(" + kind + ", " + position + ", " + value + ")";
    }
}
