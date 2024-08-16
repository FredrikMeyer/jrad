package net.fredrikmeyer.jisp.environment;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.fredrikmeyer.jisp.BuiltInProcedure;
import net.fredrikmeyer.jisp.LispValue;
import net.fredrikmeyer.jisp.NumberValue;

public class StandardEnvironment implements Environment {

    private Environment parent;

    private final Map<String, LispValue> env = new HashMap<>() {{
        put("+", new BuiltInProcedure() {
            @Override
            public LispValue apply(LispValue... values) {
                return new NumberValue(
                    Arrays.stream(values)
                        .map(t -> (NumberValue) t)
                        .map(NumberValue::d)
                        .reduce(0.0, Double::sum));
            }
        });
        put("*", new BuiltInProcedure() {
            @Override
            public LispValue apply(LispValue... values) {
                return new NumberValue(
                    Arrays.stream(values)
                        .map(t -> (NumberValue) t)
                        .map(NumberValue::d)
                        .reduce(1., (a, b) -> a * b));
            }
        });
    }};


    public StandardEnvironment() {
    }

    public StandardEnvironment(Environment parent) {
        this.parent = parent;
    }

    @Override
    public LispValue lookUpVariable(String name) {
        if (env.containsKey(name)) {
            return env.get(name);
        } else {
            return parent != null ? parent.lookUpVariable(name) : null;
        }
    }

    @Override
    public void setVariable(String name, LispValue value) {
        env.put(name, value);
    }

    @Override
    public Environment extendEnvironment(Map<String, LispValue> bindings) {
        StandardEnvironment newEnvironment = new StandardEnvironment(this);

        for (Map.Entry<String, LispValue> binding : bindings.entrySet()) {
            newEnvironment.setVariable(binding.getKey(), binding.getValue());
        }

        return newEnvironment;
    }
}
