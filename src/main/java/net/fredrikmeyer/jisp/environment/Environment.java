package net.fredrikmeyer.jisp.environment;

import java.util.Map;
import net.fredrikmeyer.jisp.LispValue;

public interface Environment {
    LispValue lookUpVariable(String name);
    void setVariable(String name, LispValue value);
    Environment extendEnvironment(Map<String, LispValue> bindings);
}
