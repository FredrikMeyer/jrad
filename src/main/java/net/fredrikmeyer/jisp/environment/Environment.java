package net.fredrikmeyer.jisp.environment;

import java.util.Map;
import net.fredrikmeyer.jisp.LispExpression;

public interface Environment {
    LispExpression lookUpVariable(String name);
    void setVariable(String name, LispExpression value);
    Environment extendEnvironment(Map<String, LispExpression> bindings);
}
