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
}