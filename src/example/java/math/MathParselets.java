package math;

import com.github.natanbc.pratt.InfixParselet;
import com.github.natanbc.pratt.Parser;
import com.github.natanbc.pratt.PrefixParselet;
import com.github.natanbc.pratt.Token;
import math.ast.BinaryOperationNode;
import math.ast.Node;
import math.ast.NumberNode;
import math.ast.UnaryOperationNode;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public class MathParselets {
    public static final PrefixParselet<Void, Node> NUMBER = (__1, __2, token) ->
            new NumberNode(Double.parseDouble(token.value()));
    
    public static final PrefixParselet<Void, Node> NEG = (ctx, parser, __) ->
            new UnaryOperationNode(parser.parseExpression(ctx, Precedence.NEG), i -> -i);
    
    public static final PrefixParselet<Void, Node> PAREN = (ctx, parser, __) -> {
        Node expr = parser.parseExpression(ctx);
        parser.expect(MathTokenKind.RIGHT_PAREN);
        return expr;
    };
    
    public static final InfixParselet<Void, Node> SUM = new BinaryOperator(Precedence.SUM, (a, b) -> a + b);
    public static final InfixParselet<Void, Node> SUB = new BinaryOperator(Precedence.SUM, (a, b) -> a - b);
    public static final InfixParselet<Void, Node> MUL = new BinaryOperator(Precedence.MUL, (a, b) -> a * b);
    public static final InfixParselet<Void, Node> DIV = new BinaryOperator(Precedence.MUL, (a, b) -> a / b);
    
    
    private static class BinaryOperator implements InfixParselet<Void, Node> {
        private final int precedence;
        private final BiFunction<Double, Double, Double> merger;
    
        private BinaryOperator(int precedence, BiFunction<Double, Double, Double> merger) {
            this.precedence = precedence;
            this.merger = merger;
        }
    
        @Nonnegative
        @CheckReturnValue
        @Override
        public int precedence() {
            return precedence;
        }
    
        @Nonnull
        @CheckReturnValue
        @Override
        public Node parse(Void context, @Nonnull Parser<Void, Node> parser, @Nonnull Node left, @Nonnull Token token) {
            return new BinaryOperationNode(
                    left,
                    parser.parseExpression(context, precedence),
                    merger
            );
        }
    }
}
