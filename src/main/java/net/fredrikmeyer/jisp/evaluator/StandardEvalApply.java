package net.fredrikmeyer.jisp.evaluator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.fredrikmeyer.jisp.LispExpression;
import net.fredrikmeyer.jisp.LispList;
import net.fredrikmeyer.jisp.LispLiteral;
import net.fredrikmeyer.jisp.evaluator.SyntacticForm.Assignment;
import net.fredrikmeyer.jisp.evaluator.SyntacticForm.Conditional;
import net.fredrikmeyer.jisp.evaluator.SyntacticForm.FunctionApplication;
import net.fredrikmeyer.jisp.evaluator.SyntacticForm.Lambda;
import net.fredrikmeyer.jisp.evaluator.SyntacticForm.Quotation;
import net.fredrikmeyer.jisp.evaluator.SyntacticForm.SelfEvaluating;
import net.fredrikmeyer.jisp.evaluator.SyntacticForm.Sequence;
import net.fredrikmeyer.jisp.evaluator.SyntacticForm.Set;
import net.fredrikmeyer.jisp.evaluator.SyntacticForm.Variable;
import net.fredrikmeyer.jisp.LispExpression.LispSymbol;
import net.fredrikmeyer.jisp.LispExpression.Procedure;
import net.fredrikmeyer.jisp.LispExpression.Procedure.BuiltInProcedure;
import net.fredrikmeyer.jisp.LispExpression.Procedure.UserProcedure;
import net.fredrikmeyer.jisp.LispLiteral.BoolValue;
import net.fredrikmeyer.jisp.LispLiteral.NumberLiteral;
import net.fredrikmeyer.jisp.LispLiteral.StringLiteral;
import net.fredrikmeyer.jisp.environment.Environment;

public class StandardEvalApply implements EvalApply {

    @Override
    public LispExpression eval(LispExpression expression, Environment environment) {
        Objects.requireNonNull(expression); // (+ 1 2)

        var parsed = parseExpression(expression);

        return switch (parsed) {
            case SelfEvaluating selfEvaluating -> selfEvaluating.literal();

            case Variable(LispSymbol(var name)) -> {
                LispExpression lispValue = environment.lookUpVariable(name);
                if (lispValue == null) {
                    yield new LispSymbol("nil");
                }
                yield lispValue;
            }
            case Quotation(LispExpression exp) -> exp;

            case Assignment(var symbol, var body) -> {
                var name = symbol.name();

                environment.setVariable(name, eval(body, environment));

                yield new LispSymbol("ok");
            }

            case Set(LispSymbol(var name), var value) -> {
                if (environment.lookUpVariable(name) == null) {
                    throw new RuntimeException("Cannot set! non-existing symbol.");
                } else {
                    environment.setVariable(name, eval(value, environment));

                    yield new LispSymbol("ok");
                }
            }

            // (if #t 1 2)
            case Conditional(var condition, var then, var otherwise) -> {
                var evaluatedCondition = eval(condition, environment);

                if (isTrueIsh(evaluatedCondition)) {
                    yield eval(then, environment);
                } else {
                    yield eval(otherwise, environment);
                }
            }

            // (f 1 2)
            case FunctionApplication(var procedure, var arguments) -> {
                var evaluatedProcedure = eval(procedure, environment);

                if (!(evaluatedProcedure instanceof Procedure p)) {
                    throw new RuntimeException(
                        "Procedure expected, got: " + procedure + ". Expression: " + expression);
                }

                var evaluatedArguments = arguments
                    .stream()
                    .map(el -> eval(el, environment))
                    .toList();

                yield apply(p, evaluatedArguments);
            }
            // (lambda (x) (+ x 1))
            case Lambda(var arguments, var body) -> {
                List<String> args = arguments.stream().map(LispSymbol::name).toList();

                yield new UserProcedure(environment, args, body);
            }

            case Sequence(var forms) -> {
                LispExpression lastVal = null;
                for (var e : forms) {
                    lastVal = eval(e, environment);
                }

                yield Objects.requireNonNull(lastVal);
            }
        };
    }

    @Override
    public LispExpression apply(Procedure procedure, List<LispExpression> arguments) {
        return switch (procedure) {
            case BuiltInProcedure builtInProcedure ->
                builtInProcedure.apply(arguments.toArray(LispExpression[]::new));
            case UserProcedure(Environment env, var args, var body) -> {
                var newFrame = IntStream.range(0, arguments.size())
                    .boxed()
                    .collect(Collectors.toMap(args::get, arguments::get));
                var newEnv = env.extendEnvironment(newFrame);

                yield eval(body, newEnv);
            }
        };
    }


    private SyntacticForm parseExpression(LispExpression expression) {
        // Is literal, return SyntacticForm of type SelfEvaluating
        if (expression instanceof LispLiteral literal) {
            return new SelfEvaluating(literal);
        }

        // Is variable
        if (expression instanceof LispSymbol s) {
            return new Variable(s);
        }

        // Starts with ' or (quote
        if (parseQuotation(expression) instanceof Quotation q) {
            return q;
        }

        // Example: (define a (+ 1 2))
        if (parseAssignment(expression) instanceof Assignment assignment) {
            return assignment;
        }

        if (parseSet(expression) instanceof Set set) {
            return set;
        }

        // Example: (begin (define b 2) (+ b 3))
        if (parseSequence(expression) instanceof Sequence sequence) {
            return sequence;
        }

        // Example: (lambda (a b) (+ a b))
        if (parseLambda(expression) instanceof Lambda lambda) {
            return lambda;
        }

        // Example: (if <test> <then> <otherwise>)
        if (parseConditional(expression) instanceof Conditional conditional) {
            return conditional;
        }

        // Anything else is a function application: (f arg1 arg2)
        if (parseFunctionApplication(
            expression) instanceof FunctionApplication functionApplication) {
            return functionApplication;
        }

        throw new RuntimeException("Should not get here: " + expression);
    }

    private boolean isTrueIsh(LispExpression expression) {
        switch (expression) {
            case LispSymbol(var name) -> {
                return !name.equals("nil");
            }
            case Procedure _ -> {
                return true;
            }
            case LispList lispList -> {
                return lispList.length() > 0;
            }
            case LispLiteral literal -> {
                switch (literal) {
                    case BoolValue boolValue -> {
                        return boolValue.value();
                    }
                    case NumberLiteral _, StringLiteral _ -> {
                        return true;
                    }
                }
            }
        }
    }

    private Conditional parseConditional(LispExpression expression) {
        if (expression instanceof LispList lispList) {
            if (lispList.length() <= 3) {
                return null;
            }

            if (!(lispList.car() instanceof LispSymbol symbol)) {
                return null;
            }

            return symbol.name().equals("if") ? new Conditional(lispList.cadr(), lispList.caddr(),
                lispList.cadddr()) : null;
        }
        return null;
    }

    private FunctionApplication parseFunctionApplication(LispExpression expression) {
        if (expression instanceof LispList l && l.length() > 0) {
            return new FunctionApplication(l.car(), l.cdr().elements());
        }
        return null;
    }

    private Sequence parseSequence(LispExpression expression) {
        if (expression instanceof LispList lispList) {
            if (lispList.length() < 1) {
                return null;
            }

            if (!(lispList.car() instanceof LispSymbol symbol)) {
                return null;
            }

            return symbol.name().equals("begin") ? new Sequence(lispList.cdr().elements()) : null;
        }
        return null;
    }

    private Quotation parseQuotation(LispExpression expression) {
        return expression instanceof LispList l && l.car() instanceof LispSymbol symbol
               && symbol.name().equals("quote") ? new Quotation(l.cadr()) : null;
    }

    private Assignment parseAssignment(LispExpression expression) {
        if (expression instanceof LispList lispList) {
            if (lispList.length() != 3) {
                return null;
            }

            if ((!(lispList.car() instanceof LispSymbol(String name)))) {
                return null;
            }

            if (!name.equals("define")) {
                return null;
            }

            if (!(lispList.cadr() instanceof LispSymbol var)) {
                return null;
            }

            var rest = lispList.caddr();

            return new Assignment(var, rest);
        }
        return null;
    }

    private Set parseSet(LispExpression expression) {
        if (expression instanceof LispList lispList) {
            if (lispList.length() != 3) {
                return null;
            }

            if (!(lispList.car() instanceof LispSymbol(String name))) {
                return null;
            }

            if (!name.equals("set!")) {
                return null;
            }

            if (!(lispList.cadr() instanceof LispSymbol variable)) {
                return null;
            }

            return new Set(variable, lispList.caddr());
        }
        return null;
    }

    private Lambda parseLambda(LispExpression expression) {
        if (expression instanceof LispList lispList) {
            if (!(lispList.car() instanceof LispSymbol(String name))) {
                return null;
            }

            if (!(name.equals("lambda"))) {
                return null;
            }

            if (!(lispList.cadr() instanceof LispList arguments)) {
                return null;
            }

            if (!arguments.elements().stream().allMatch(e -> e instanceof LispSymbol)) {
                throw new IllegalArgumentException("All args must be symbol");
            }

            return new Lambda(arguments.elements().stream().map(e -> (LispSymbol) e).toList(),
                lispList.caddr());
        }
        return null;
    }
}
