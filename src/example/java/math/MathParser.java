package math;

import com.github.natanbc.pratt.Parser;
import math.ast.Node;

import javax.annotation.Nonnull;
import java.io.PushbackReader;
import java.io.StringReader;

public class MathParser extends Parser<Void, Node> {
    public MathParser(@Nonnull String expression) {
        super(new MathLexer(new PushbackReader(new StringReader(expression))));
        register(MathTokenKind.NUMBER, MathParselets.NUMBER);
        register(MathTokenKind.MINUS, MathParselets.NEG);
        register(MathTokenKind.LEFT_PAREN, MathParselets.PAREN);
        register(MathTokenKind.PLUS, MathParselets.SUM);
        register(MathTokenKind.MINUS, MathParselets.SUB);
        register(MathTokenKind.ASTERISK, MathParselets.MUL);
        register(MathTokenKind.SLASH, MathParselets.DIV);
    }
    
    public static void main(String[] args) {
        System.out.println(new MathParser("1 + 2.5 * (0.1 + 0.2) + .5").parseExpression(null).eval());
    }
}
