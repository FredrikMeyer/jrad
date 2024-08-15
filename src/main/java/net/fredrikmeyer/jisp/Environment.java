package net.fredrikmeyer.jisp;

import java.util.Map;

public interface Environment {
    LispValue lookUpVariable(String name);
    void setVariable(String name, LispValue value);
    Environment extendEnvironment(Map<String, LispValue> bindings);
}
