package net.fredrikmeyer.jisp;

import java.util.List;

public interface IEvalApply {
    LispValue eval(LispExpression expression, Environment environment);

    LispValue apply(Procedure procedure, List<LispValue> arguments);
}
