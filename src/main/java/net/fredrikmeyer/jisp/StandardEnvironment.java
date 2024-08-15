package net.fredrikmeyer.jisp;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StandardEnvironment implements Environment {

    private final Map<String, LispValue> env = new HashMap<>();

    public StandardEnvironment() {
        var plus = new BuiltInProcedure() {
            @Override
            LispValue apply(LispValue... values) {
                return new NumberValue(
                    Arrays.stream(values)
                        .map(t -> (NumberValue) t)
                        .map(NumberValue::d)
                        .reduce(0.0, Double::sum));
            }
        };

        var multiplication = new BuiltInProcedure() {
            @Override
            LispValue apply(LispValue... values) {
                return new NumberValue(
                    Arrays.stream(values)
                        .map(t -> (NumberValue) t)
                        .map(NumberValue::d)
                        .reduce(1., (a, b) -> a * b));
            }
        };

        env.put("+", plus);
        env.put("*", multiplication);
    }

    @Override
    public LispValue lookUpVariable(String name) {
        return env.get(name);
    }

    @Override
    public void setVariable(String name, LispValue value) {
        env.put(name, value);
    }

    @Override
    public Environment extendEnvironment(Map<String, LispValue> bindings) {
        return null;
    }
}
