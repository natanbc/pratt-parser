package math.ast;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.function.DoubleUnaryOperator;

public class UnaryOperationNode implements Node {
    private final Node target;
    private final DoubleUnaryOperator operator;
    
    public UnaryOperationNode(@Nonnull Node target, @Nonnull DoubleUnaryOperator operator) {
        this.target = target;
        this.operator = operator;
    }
    
    @CheckReturnValue
    @Override
    public double eval() {
        return operator.applyAsDouble(target.eval());
    }
}
