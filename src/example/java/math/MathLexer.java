package math;

import com.github.natanbc.pratt.Lexer;
import com.github.natanbc.pratt.Position;
import com.github.natanbc.pratt.Token;
import com.github.natanbc.pratt.TokenKind;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.io.PushbackReader;

public class MathLexer extends Lexer {
    public MathLexer(@Nonnull PushbackReader reader) {
        super(reader);
    }
    
    @Nonnull
    @CheckReturnValue
    @Override
    protected Token parse() {
        int ch = read(true);
        switch(ch) {
            case -1: return new Token(MathTokenKind.EOF, pos(), "<EOF>");
            case '+': return new Token(MathTokenKind.PLUS, pos(), "+");
            case '-': return new Token(MathTokenKind.MINUS, pos(), "-");
            case '*': return new Token(MathTokenKind.ASTERISK, pos(), "*");
            case '/': return new Token(MathTokenKind.SLASH, pos(), "/");
            case '(': return new Token(MathTokenKind.LEFT_PAREN, pos(), "(");
            case ')': return new Token(MathTokenKind.RIGHT_PAREN, pos(), ")");
            default: {
                if(Character.isDigit(ch)) {
                    return new Token(MathTokenKind.NUMBER, pos(), readNumber((char)ch));
                } else if(ch == '.') {
                    if(Character.isDigit(peek(false))) {
                        return new Token(MathTokenKind.NUMBER, pos(), readNumber((char)ch));
                    }
                }
                Position pos = pos();
                String s = prettyContext(pos, 1);
                throw new IllegalArgumentException("Unexpected character '" + ((char)ch) + "' at line " +
                        pos.line() + ", column " + pos.column() + "\n\n" + s);
            }
        }
    }
    
    @Nonnull
    @CheckReturnValue
    @Override
    protected TokenKind eofKind() {
        return MathTokenKind.EOF;
    }
    
    @Nonnull
    @CheckReturnValue
    private String readNumber(char start) {
        StringBuilder sb = new StringBuilder().append(start);
        boolean point = start == '.';
        while(true) {
            int ch = read(false);
            if(Character.isDigit(ch)) {
                sb.append((char)ch);
            } else if(ch == '.') {
                if(point) {
                    return sb.toString();
                }
                ch = read(false);
                unread(ch);
                if(!Character.isDigit(ch)) {
                    return sb.toString();
                }
                sb.append('.');
                point = true;
            } else {
                if(ch != -1) {
                    unread(ch);
                }
                return sb.toString();
            }
        }
    }
}
