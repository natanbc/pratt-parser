package math.ast;

import javax.annotation.CheckReturnValue;

public class NumberNode implements Node {
    private final double value;
    
    public NumberNode(double value) {
        this.value = value;
    }
    
    @CheckReturnValue
    @Override
    public double eval() {
        return value;
    }
}
