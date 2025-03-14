package net.fredrikmeyer.jisp;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fredrikmeyer.jisp.LispExpression.LispSymbol;
import net.fredrikmeyer.jisp.LispExpression.Procedure;
import net.fredrikmeyer.jisp.LispLiteral.NumberLiteral;
import net.fredrikmeyer.jisp.LispLiteral.StringLiteral;
import net.fredrikmeyer.jisp.environment.Environment;
import net.fredrikmeyer.jisp.evaluator.StandardEvalApply;
import org.junit.jupiter.api.Test;

class StandardEvalApplyTest {

    @Test
    public void stringsSelfEvaluate() {
        StandardEvalApply evalApply = new StandardEvalApply();

        var res = evalApply.eval(new LispLiteral.StringLiteral("hei"), dummyEnvironment());

        assertThat(res).isEqualTo(new StringLiteral("hei"));
    }

    private static Environment dummyEnvironment() {
        return new Environment() {
            @Override
            public LispExpression lookUpVariable(String name) {
                return null;
            }

            @Override
            public void setVariable(String name, LispExpression value) {

            }

            @Override
            public Environment extendEnvironment(Map<String, LispExpression> bindings) {
                return null;
            }
        };
    }

    @Test
    public void numbersSelfEvaluate() {
        StandardEvalApply evalApply = new StandardEvalApply();

        var res = evalApply.eval(new LispLiteral.NumberLiteral(120.4), dummyEnvironment());

        assertThat(res).isEqualTo(new NumberLiteral(120.4));
    }

    @Test
    public void canDefineVariables() {
        StandardEvalApply evalApply = new StandardEvalApply();

        Environment environment = new Environment() {
            private final Map<String, LispExpression> env = new HashMap<>();

            @Override
            public LispExpression lookUpVariable(String name) {
                return env.get(name);
            }

            @Override
            public void setVariable(String name, LispExpression value) {
                env.put(name, value);
            }

            @Override
            public Environment extendEnvironment(Map<String, LispExpression> bindings) {
                return null;
            }
        };

        evalApply.eval(new LispList(
                List.of(new LispSymbol("define"), new LispSymbol("x"),
                    new NumberLiteral(120.4))),
            environment);

        assertThat(environment.lookUpVariable("x")).isEqualTo(new NumberLiteral(120.4));
    }

    @Test
    public void defineLambda() {
        StandardEvalApply evalApply = new StandardEvalApply();

        // (lambda (x) (+ x 2))
        var res = evalApply.eval(new LispList(
            List.of(new LispSymbol("lambda"), new LispList(List.of(new LispSymbol("x"))),
                new LispList(List.of(new LispSymbol("+"), new LispSymbol("x"),
                    new LispLiteral.NumberLiteral(2.0))))), dummyEnvironment());

        assertThat(res).isInstanceOf(Procedure.class);
    }

    @Test
    public void quotedReturnsList() {
        StandardEvalApply evalApply = new StandardEvalApply();

        var res = evalApply.eval(new LispList(
                List.of(new LispSymbol("quote"), new LispList(List.of(new LispSymbol("x"))))),
            dummyEnvironment());

        assertThat(res).isEqualTo(new LispList(List.of(new LispSymbol("x"))));
    }

    @Test
    public void defnDefinesFunction() {
        StandardEvalApply evalApply = new StandardEvalApply();

        Environment environment = new Environment() {
            private final Map<String, LispExpression> env = new HashMap<>();

            @Override
            public LispExpression lookUpVariable(String name) {
                return env.get(name);
            }

            @Override
            public void setVariable(String name, LispExpression value) {
                env.put(name, value);
            }

            @Override
            public Environment extendEnvironment(Map<String, LispExpression> bindings) {
                Map<String, LispExpression> newEnv = new HashMap<>(env);
                newEnv.putAll(bindings);

                return new Environment() {
                    @Override
                    public LispExpression lookUpVariable(String name) {
                        return newEnv.get(name);
                    }

                    @Override
                    public void setVariable(String name, LispExpression value) {
                        newEnv.put(name, value);
                    }

                    @Override
                    public Environment extendEnvironment(Map<String, LispExpression> moreBindings) {
                        return null;
                    }
                };
            }
        };

        // Define a function: (defn add (x y) (+ x y))
        evalApply.eval(new LispList(
                List.of(new LispSymbol("defn"), new LispSymbol("add"),
                    new LispList(List.of(new LispSymbol("x"), new LispSymbol("y"))),
                    new LispList(List.of(new LispSymbol("+"), new LispSymbol("x"), new LispSymbol("y"))))),
            environment);

        // Verify that the function is defined
        LispExpression addFunction = environment.lookUpVariable("add");
        assertThat(addFunction).isInstanceOf(Procedure.class);

        // Set up the environment with a + function for testing
        environment.setVariable("+", new Procedure.BuiltInProcedure("+") {
            @Override
            public LispExpression apply(LispExpression... values) {
                if (values.length != 2 || !(values[0] instanceof NumberLiteral) || !(values[1] instanceof NumberLiteral)) {
                    throw new IllegalArgumentException("+ expects two numbers");
                }
                double a = ((NumberLiteral) values[0]).value();
                double b = ((NumberLiteral) values[1]).value();
                return new NumberLiteral(a + b);
            }
        });

        // Call the function: (add 2 3)
        var result = evalApply.eval(new LispList(
                List.of(new LispSymbol("add"), new NumberLiteral(2.0), new NumberLiteral(3.0))),
            environment);

        // Verify the result
        assertThat(result).isEqualTo(new NumberLiteral(5.0));
    }
}
