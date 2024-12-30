package net.fredrikmeyer.jisp.environment;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import net.fredrikmeyer.jisp.LispExpression;
import net.fredrikmeyer.jisp.LispExpression.Procedure.BuiltInProcedure;
import net.fredrikmeyer.jisp.LispList;
import net.fredrikmeyer.jisp.LispLiteral;
import net.fredrikmeyer.jisp.LispLiteral.BoolValue;
import net.fredrikmeyer.jisp.LispLiteral.NumberLiteral;

public class StandardEnvironment implements Environment {

    private Environment parent;

    private final Map<String, LispExpression> env = new HashMap<>() {{
        put("+", new BuiltInProcedure("+") {
            @Override
            public LispExpression apply(LispExpression... values) {
                if (!(Arrays.stream(values).map(LispExpression::getClass)
                    .allMatch(c -> c == NumberLiteral.class))) {
                    throw new RuntimeException(
                        "Not all values are numbers: " + Arrays.toString(values));
                }

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
                // true if values are strictly increasing
                boolean isDecreasing = true;
                NumberLiteral prev = null;
                for (LispExpression val : values) {
                    if (prev != null) {
                        isDecreasing = isDecreasing && ((NumberLiteral) val).value() > prev.value();
                    }
                    prev = (NumberLiteral) val;
                }
                return new BoolValue(isDecreasing);
            }
        });

        put("list", new BuiltInProcedure("list") {
            @Override
            public LispExpression apply(LispExpression... values) {
                return new LispList(List.of(values));
            }
        });

        put("abs", new BuiltInProcedure("abs") {

            @Override
            public LispExpression apply(LispExpression... values) {
                assert values.length == 1;

                if (Objects.requireNonNull(values[0]) instanceof LispLiteral lispLiteral) {
                    if (lispLiteral instanceof NumberLiteral numberLiteral) {
                        return new NumberLiteral(Math.abs(numberLiteral.value()));
                    }
                    throw new IllegalStateException("Unexpected value: " + lispLiteral);
                }
                throw new IllegalStateException("Unexpected value: " + values[0]);
            }

            ;
        });

        put("/", new BuiltInProcedure("/") {
            @Override
            public LispExpression apply(LispExpression... values) {
                return new NumberLiteral(
                    ((NumberLiteral) values[0]).value() / ((NumberLiteral) values[1]).value());
            }
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

        for (Entry<String, LispExpression> binding : bindings.entrySet()) {
            newEnvironment.setVariable(binding.getKey(), binding.getValue());
        }

        return newEnvironment;
    }

    @Override
    public String toString() {
        return "StandardEnvironment{" +
               "env=" + env +
               ", parent=" + parent +
               '}';

    }
}
