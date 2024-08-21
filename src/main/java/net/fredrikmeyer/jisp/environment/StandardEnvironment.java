package net.fredrikmeyer.jisp.environment;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.fredrikmeyer.jisp.BuiltInProcedure;
import net.fredrikmeyer.jisp.LispExpression;
import net.fredrikmeyer.jisp.LispLiteral.BoolValue;
import net.fredrikmeyer.jisp.LispLiteral.NumberLiteral;

public class StandardEnvironment implements Environment {

    private Environment parent;

    private final Map<String, LispExpression> env = new HashMap<>() {{
        put("+", new BuiltInProcedure("+") {
            @Override
            public LispExpression apply(LispExpression... values) {
                return new NumberLiteral(
                    Arrays.stream(values)
                        .map(t -> (NumberLiteral) t)
                        .map(NumberLiteral::value)
                        .reduce(0.0, Double::sum));
            }
        });

        put("-", new BuiltInProcedure("-") {
            @Override
            public LispExpression apply(LispExpression... values) {
                return new NumberLiteral(
                    ((NumberLiteral) values[0]).value() - Arrays.stream(values)
                        .skip(1)
                        .map(t -> (NumberLiteral) t)
                        .map(NumberLiteral::value)
                        .reduce(0.0, Double::sum));
            }
        });

        put("*", new BuiltInProcedure("*") {
            @Override
            public LispExpression apply(LispExpression... values) {
                return new NumberLiteral(
                    Arrays.stream(values)
                        .map(t -> (NumberLiteral) t)
                        .map(NumberLiteral::value)
                        .reduce(1., (a, b) -> a * b));
            }
        });

        put("=", new BuiltInProcedure("=") {
            @Override
            public LispExpression apply(LispExpression... values) {
                return new BoolValue(Arrays.stream(values).distinct().count() <= 1);
            }
        });

        put("<", new BuiltInProcedure("<") {
            @Override
            public LispExpression apply(LispExpression... values) {
                // true if values are strictly decreasing
                boolean isDecreasing = true;
                NumberLiteral prev = null;
                for (LispExpression val : values) {
                    if (prev != null) {
                        isDecreasing = isDecreasing && ((NumberLiteral) val).value() < prev.value();
                    }
                    prev = (NumberLiteral) val;
                }
                return new BoolValue(isDecreasing);
            }

            ;
        });
    }};


    public StandardEnvironment() {
    }

    public StandardEnvironment(Environment parent) {
        this.parent = parent;
    }

    @Override
    public LispExpression lookUpVariable(String name) {
        if (env.containsKey(name)) {
            return env.get(name);
        } else {
            return parent != null ? parent.lookUpVariable(name) : null;
        }
    }

    @Override
    public void setVariable(String name, LispExpression value) {
        env.put(name, value);
    }

    @Override
    public Environment extendEnvironment(Map<String, LispExpression> bindings) {
        StandardEnvironment newEnvironment = new StandardEnvironment(this);

        for (Map.Entry<String, LispExpression> binding : bindings.entrySet()) {
            newEnvironment.setVariable(binding.getKey(), binding.getValue());
        }

        return newEnvironment;
    }
}
