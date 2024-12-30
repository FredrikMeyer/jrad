package net.fredrikmeyer.jisp.evaluator;

import java.util.List;
import net.fredrikmeyer.jisp.LispExpression;
import net.fredrikmeyer.jisp.LispExpression.Procedure;
import net.fredrikmeyer.jisp.environment.Environment;

public interface EvalApply {

    LispExpression eval(LispExpression expression, Environment environment);

    LispExpression apply(Procedure procedure, List<LispExpression> arguments);
}
