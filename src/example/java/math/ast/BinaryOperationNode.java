package math.ast;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public class BinaryOperationNode implements Node {
    private final Node left, right;
    private final BiFunction<Double, Double, Double> merger;
    
    public BinaryOperationNode(@Nonnull Node left, @Nonnull Node right, @Nonnull BiFunction<Double, Double, Double> merger) {
        this.left = left;
        this.right = right;
        this.merger = merger;
    }
    
    @CheckReturnValue
    @Override
    public double eval() {
        return merger.apply(left.eval(), right.eval());
    }
}
