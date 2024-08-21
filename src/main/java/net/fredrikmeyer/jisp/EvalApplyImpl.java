package net.fredrikmeyer.jisp;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.fredrikmeyer.jisp.LispExpression.Procedure;
import net.fredrikmeyer.jisp.LispLiteral.BoolValue;
import net.fredrikmeyer.jisp.environment.Environment;
import org.jetbrains.annotations.NotNull;

public class EvalApplyImpl implements IEvalApply {

    @NotNull
    @Override
    public LispExpression eval(LispExpression expression, Environment environment) {
        Objects.requireNonNull(expression);

        if (expression instanceof LispLiteral literal) {
            return literal;
        } else if (isVariable(expression)) {
            LispExpression lispValue = environment.lookUpVariable(((LispSymbol) expression).value());
            if (lispValue == null) {
                return new LispSymbol("nil");
            }
            return lispValue;
        } else if (isQuoted(expression)) {
            return ((LispList) expression).cadr();
        } else if (isAssignment(expression)) {
            var name = ((LispSymbol) ((LispList) expression).cadr()).value();
            var value = ((LispList) expression).caddr();
            environment.setVariable(name, eval(value, environment));

            // TODO: lage keywords for ok, nil, osv
            return new LispSymbol("ok");
        } else if (isSequence(expression)) {
            var expressions = ((LispList) expression).cdr().elements();

            LispExpression lastVal = null;
            for (var e : expressions) {
                lastVal = eval(e, environment);
            }

            return Objects.requireNonNull(lastVal);
        } else if (isLambda(expression)) {
            // Ugly, but this is Java :)
            var arguments = ((LispList) ((LispList) expression).cadr())
                .elements().stream()
                .map(el -> ((LispSymbol) el).value()).toList();
            var body = ((LispList) expression).cdr().cadr();
            return new UserProcedure(environment, arguments, body);
        } else if (isConditional(expression)) {
            var condition = eval(((LispList) expression).cadr(), environment);

            if (condition instanceof BoolValue v) {
                if (v.value()) {
                    return eval(((LispList) expression).caddr(), environment);
                } else {
                    return eval(((LispList) expression).cadddr(), environment);
                }
            }
        } else if ((expression instanceof LispList l && l.length() > 0)) {
            // We are a function application

            var procedure = eval(l.car(), environment);

            if (!(procedure instanceof Procedure p)) {
                throw new RuntimeException("...");
            }

            var arguments = l.cdr().elements()
                .stream()
                .map(el -> eval(el, environment))
                .toList();

            return apply(p, arguments);
        }

        throw new RuntimeException("Should not get here: " + expression);
    }

    private boolean isConditional(LispExpression expression) {
        if (expression instanceof LispList lispList) {
            if (lispList.length() <= 3) {
                return false;
            }

            if (!(lispList.car() instanceof LispSymbol symbol)) {
                return false;
            }

            return symbol.value().equals("if");
        }
        return false;
    }

    private boolean isSequence(LispExpression expression) {
        // TODO extract logic
        if (expression instanceof LispList lispList) {
            if (lispList.length() < 1) {
                return false;
            }

            if (!(lispList.car() instanceof LispSymbol symbol)) {
                return false;
            }

            return symbol.value().equals("begin");
        }
        return false;
    }

    @Override
    public LispExpression apply(Procedure procedure, List<LispExpression> arguments) {
        return switch (procedure) {
            case BuiltInProcedure builtInProcedure ->
                builtInProcedure.apply(arguments.toArray(LispExpression[]::new));
            case UserProcedure userProcedure -> {
                var newFrame = IntStream.range(0, arguments.size())
                    .boxed()
                    .collect(Collectors.toMap(userProcedure.arguments()::get, arguments::get));
                var newEnv = userProcedure.environment().extendEnvironment(newFrame);

                yield eval(userProcedure.body(), newEnv);
            }
        };
    }

    private boolean isVariable(LispExpression expression) {
        return expression instanceof LispSymbol;
    }

    private boolean isQuoted(LispExpression expression) {
        return startsWithGivenSymbol(expression, "quote");
    }

    private boolean isAssignment(LispExpression expression) {
        if (expression instanceof LispList lispList) {
            if (lispList.length() != 3) {
                return false;
            }

            if (!(lispList.car() instanceof LispSymbol symbol)) {
                return false;
            }

            return symbol.value().equals("set!");
        }
        return false;
    }

    private boolean isLambda(LispExpression expression) {
        if (expression instanceof LispList lispList) {
            if (!(lispList.car() instanceof LispSymbol symbol)) {
                return false;
            }

            if (!(symbol.value().equals("lambda"))) {
                return false;
            }

            if (!(lispList.cadr() instanceof LispList arguments)) {
                return false;
            }

            if (!arguments.elements().stream().allMatch(e -> e instanceof LispSymbol)) {
                throw new IllegalArgumentException("All args must be symbol");
            }

            return true;
        }
        return false;
    }

    private boolean startsWithGivenSymbol(LispExpression expression, String symbol) {
        if (expression instanceof LispList lispList) {
            if (!(lispList.car() instanceof LispSymbol s)) {
                return false;
            }

            return s.value().equals(symbol);
        }
        return false;
    }
}
