package math.ast;

import javax.annotation.CheckReturnValue;

public interface Node {
    @CheckReturnValue
    double eval();
}
