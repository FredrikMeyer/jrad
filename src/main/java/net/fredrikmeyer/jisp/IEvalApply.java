package net.fredrikmeyer.jisp;

import java.util.List;
import net.fredrikmeyer.jisp.environment.Environment;

public interface IEvalApply {
    LispValue eval(LispExpression expression, Environment environment);

    LispValue apply(Procedure procedure, List<LispValue> arguments);
}
