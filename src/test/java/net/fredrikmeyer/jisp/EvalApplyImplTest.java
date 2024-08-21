package net.fredrikmeyer.jisp;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fredrikmeyer.jisp.LispExpression.Procedure;
import net.fredrikmeyer.jisp.LispLiteral.NumberLiteral;
import net.fredrikmeyer.jisp.LispLiteral.StringLiteral;
import net.fredrikmeyer.jisp.environment.Environment;
import org.junit.jupiter.api.Test;

class EvalApplyImplTest {

    @Test
    public void stringsSelfEvaluate() {
        EvalApplyImpl evalApply = new EvalApplyImpl();

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
        EvalApplyImpl evalApply = new EvalApplyImpl();

        var res = evalApply.eval(new LispLiteral.NumberLiteral(120.4), dummyEnvironment());

        assertThat(res).isEqualTo(new NumberLiteral(120.4));
    }

    @Test
    public void canDefineVariables() {
        EvalApplyImpl evalApply = new EvalApplyImpl();

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

        var res = evalApply.eval(new LispList(
                List.of(new LispSymbol("set!"), new LispSymbol("x"),
                    new LispLiteral.NumberLiteral(120.4))),
            environment);

        assertThat(environment.lookUpVariable("x")).isEqualTo(new NumberLiteral(120.4));
    }

    @Test
    public void defineLambda() {
        EvalApplyImpl evalApply = new EvalApplyImpl();

        // (lambda (x) (+ x 2))
        var res = evalApply.eval(new LispList(
            List.of(new LispSymbol("lambda"), new LispList(List.of(new LispSymbol("x"))),
                new LispList(List.of(new LispSymbol("+"), new LispSymbol("x"),
                    new LispLiteral.NumberLiteral(2.0))))), dummyEnvironment());

        assertThat(res).isInstanceOf(Procedure.class);
    }
}