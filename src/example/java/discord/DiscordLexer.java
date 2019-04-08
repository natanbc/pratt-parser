package discord;

import com.github.natanbc.pratt.Lexer;
import com.github.natanbc.pratt.Position;
import com.github.natanbc.pratt.Token;
import com.github.natanbc.pratt.TokenKind;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.Reader;

public class DiscordLexer extends Lexer {
    public DiscordLexer(@Nonnull Reader reader) {
        super(reader);
    }
    
    @CheckReturnValue
    @Nonnull
    @Override
    protected Token parse() {
        int ch = read(false);
        switch(ch) {
            case -1: return new Token(DiscordTokenKind.EOF, pos(), "<EOF>");
            case '<': return new Token(DiscordTokenKind.LEFT_ANGLE_BRACKET, pos(), "<");
            case '>': return new Token(DiscordTokenKind.RIGHT_ANGLE_BRACKET, pos(), ">");
            case '@': return new Token(DiscordTokenKind.AT, pos(), "@");
            case '!': return new Token(DiscordTokenKind.EXCLAMATION, pos(), "!");
            case '&': return new Token(DiscordTokenKind.AMPERSAND, pos(), "&");
            case '#': return new Token(DiscordTokenKind.HASH, pos(), "#");
            case ':': return new Token(DiscordTokenKind.COLON, pos(), ":");
            default: {
                if(Character.isDigit(ch)) {
                    return readPossibleId(pos(), (char)ch);
                } else if(Character.isLetter(ch)) {
                    return new Token(DiscordTokenKind.TEXT, pos(), readName((char)ch));
                } else {
                    Position pos = pos();
                    String s = prettyContext(pos, 1);
                    throw new IllegalArgumentException("Unexpected character '" + ((char)ch) + "' at line " +
                            pos.line() + ", column " + pos.column() + "\n\n" + s);
                }
            }
        }
    }
    
    @CheckReturnValue
    @Nonnull
    @Override
    protected TokenKind eofKind() {
        return DiscordTokenKind.EOF;
    }
    
    private String readName(char start) {
        StringBuilder sb = new StringBuilder().append(start);
        int ch = read(false);
        while(ch != -1 && Character.isLetterOrDigit(ch)) {
            sb.append((char)ch);
            ch = read(false);
        }
        if(ch != -1) {
            back();
        }
        return sb.toString();
    }
    
    private Token readPossibleId(Position pos, char start) {
        DiscordTokenKind kind = DiscordTokenKind.ID;
        StringBuilder sb = new StringBuilder().append(start);
        int ch = read(false);
        while(ch != -1 && (Character.isLetterOrDigit(ch) || ch == '_')) {
            if(!Character.isDigit(ch)) {
                kind = DiscordTokenKind.TEXT;
            }
            sb.append((char)ch);
            ch = read(false);
        }
        if(ch != -1) {
            back();
        }
        String text = sb.toString();
        try {
            //noinspection ResultOfMethodCallIgnored
            Long.parseUnsignedLong(text);
        } catch(NumberFormatException e) {
            kind = DiscordTokenKind.TEXT;
        }
        return new Token(kind, pos, sb.toString());
    }
}
