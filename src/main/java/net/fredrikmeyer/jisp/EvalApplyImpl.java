package net.fredrikmeyer.jisp;

import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class EvalApplyImpl implements IEvalApply {

    @NotNull
    @Override
    public LispValue eval(LispExpression expression, Environment environment) {
        Objects.requireNonNull(expression);

        if (expression instanceof LispLiteral literal) {
            return switch (literal) {
                case LispLiteral.NumberLiteral n -> new NumberValue(n.value());
                case LispLiteral.StringLiteral s -> new StringValue(s.value());
            };
        } else if (isVariable(expression)) {
            return environment.lookUpVariable(((LispSymbol) expression).value());
        } else if (isQuoted(expression)) {
            return null; // TODO!
        } else if (isAssignment(expression)) {
            var name = ((LispSymbol) ((LispList) expression).cadr()).value();
            var value = ((LispList) expression).caddr();
            environment.setVariable(name, eval(value, environment));
            return new SymbolValue("ok");
        } else if (isLambda(expression)) {
            // Ugly, but this is Java :)
            var arguments = ((LispList) ((LispList) expression).cadr()).elements().stream()
                .map(el -> ((LispSymbol) el).value()).toList();
            var body = ((LispList) expression).cdr();
            return new UserProcedure(arguments, body);
        } else if ((expression instanceof LispList l && l.length() > 0)) {
            // We are a function application

            var procedure = eval(l.car(), environment);

            if (!(procedure instanceof Procedure p)) {
                throw new RuntimeException("...");
            }

            var arguments = l.cdr().elements().stream().map(el -> eval(el, environment)).toList();

            return apply(p, arguments);
        }

        throw new RuntimeException("Should not get here: " + expression);
    }

    @Override
    public LispValue apply(Procedure procedure, List<LispValue> arguments) {
        return switch (procedure) {
            case BuiltInProcedure builtInProcedure ->
                builtInProcedure.apply(arguments.toArray(LispValue[]::new));
            case UserProcedure userProcedure -> {
                throw new RuntimeException("should not get here");
            }
        };
    }

    private boolean isVariable(LispExpression expression) {
        return expression instanceof LispSymbol;
    }

    private boolean isQuoted(LispExpression expression) {
        // TODO!
        return false;
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
            // (lambda (x) (set! a 2) (+ x 3))

            if (!(lispList.car() instanceof LispSymbol symbol)) {
                return false;
            }

            if (!(symbol.value().equals("lambda"))) {
                return false;
            }

            if (!(lispList.cadr() instanceof LispList arguments)) {
                return false;
            }

            return arguments.elements().stream().allMatch(e -> e instanceof LispSymbol);
        }
        return false;
    }
}
