package net.fredrikmeyer.jisp;

import java.util.List;
import net.fredrikmeyer.jisp.LispExpression.Procedure;
import net.fredrikmeyer.jisp.environment.Environment;

public interface IEvalApply {

    LispExpression eval(LispExpression expression, Environment environment);

    LispExpression apply(Procedure procedure, List<LispExpression> arguments);
}
