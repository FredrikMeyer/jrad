package net.fredrikmeyer.jisp.environment;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import net.fredrikmeyer.jisp.LispExpression;
import net.fredrikmeyer.jisp.LispExpression.Procedure;
import net.fredrikmeyer.jisp.LispExpression.Procedure.BuiltInProcedure;
import net.fredrikmeyer.jisp.LispList;
import net.fredrikmeyer.jisp.LispLiteral;
import net.fredrikmeyer.jisp.LispLiteral.BoolValue;
import net.fredrikmeyer.jisp.LispLiteral.NumberLiteral;
import net.fredrikmeyer.jisp.LispLiteral.StringLiteral;
import net.fredrikmeyer.jisp.evaluator.StandardEvalApply;

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

        put("%", new BuiltInProcedure("%") {
            @Override
            public LispExpression apply(LispExpression... values) {
                if (values.length != 2) {
                    throw new RuntimeException("Modulo operation requires exactly 2 arguments");
                }
                if (!(values[0] instanceof NumberLiteral && values[1] instanceof NumberLiteral)) {
                    throw new RuntimeException("Modulo operation requires numeric arguments");
                }
                double dividend = ((NumberLiteral) values[0]).value();
                double divisor = ((NumberLiteral) values[1]).value();
                if (divisor == 0) {
                    throw new RuntimeException("Modulo by zero is undefined");
                }
                return new NumberLiteral(dividend % divisor);
            }
        });

        put("map", new BuiltInProcedure("map") {
            @Override
            public LispExpression apply(LispExpression... values) {
                if (values.length != 2) {
                    throw new RuntimeException("map requires exactly 2 arguments: a procedure and a list");
                }

                if (!(values[0] instanceof Procedure proc)) {
                    throw new RuntimeException("First argument to map must be a procedure");
                }

                if (!(values[1] instanceof LispList list)) {
                    throw new RuntimeException("Second argument to map must be a list");
                }

                List<LispExpression> mappedElements = list.elements().stream()
                    .map(element -> {
                        if (proc instanceof BuiltInProcedure builtIn) {
                            return builtIn.apply(element);
                        } else if (proc instanceof Procedure.UserProcedure userProc) {
                            return new StandardEvalApply().apply(userProc, List.of(element));
                        } else {
                            throw new RuntimeException("Unsupported procedure type: " + proc.getClass());
                        }
                    })
                    .toList();

                return new LispList(mappedElements);
            }
        });

        put("filter", new BuiltInProcedure("filter") {
            @Override
            public LispExpression apply(LispExpression... values) {
                if (values.length != 2) {
                    throw new RuntimeException("filter requires exactly 2 arguments: a predicate and a list");
                }

                if (!(values[0] instanceof Procedure proc)) {
                    throw new RuntimeException("First argument to filter must be a procedure");
                }

                if (!(values[1] instanceof LispList list)) {
                    throw new RuntimeException("Second argument to filter must be a list");
                }

                List<LispExpression> filteredElements = list.elements().stream()
                    .filter(element -> {
                        LispExpression result;
                        if (proc instanceof BuiltInProcedure builtIn) {
                            result = builtIn.apply(element);
                        } else if (proc instanceof Procedure.UserProcedure userProc) {
                            result = new StandardEvalApply().apply(userProc, List.of(element));
                        } else {
                            throw new RuntimeException("Unsupported procedure type: " + proc.getClass());
                        }

                        // Check if result is truthy using the same logic as StandardEvalApply.isTrueIsh
                        return switch (result) {
                            case LispSymbol(var name) -> !name.equals("nil");
                            case Procedure _ -> true;
                            case LispList lispList -> lispList.length() > 0;
                            case LispLiteral literal -> {
                                yield switch (literal) {
                                    case BoolValue boolValue -> boolValue.value();
                                    case NumberLiteral _, StringLiteral _ -> true;
                                };
                            }
                            case Nil _, Ok _ -> false;
                        };
                    })
                    .toList();

                return new LispList(filteredElements);
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
