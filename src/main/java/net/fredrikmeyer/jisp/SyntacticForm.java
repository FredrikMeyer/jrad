package net.fredrikmeyer.jisp;

import java.util.List;
import net.fredrikmeyer.jisp.LispExpression.LispSymbol;

sealed interface SyntacticForm {

    /// Any forms that evaluates to itself: #t, #f, numbers, strings, ...
    record SelfEvaluating(LispLiteral literal) implements SyntacticForm {

    }

    /// Variables are symbols
    record Variable(LispSymbol s) implements SyntacticForm {

    }

    /// A quoted form is not evaluated: (eval '(+ 1 2)) => (+ 1 2)
    record Quotation(LispExpression expression) implements SyntacticForm {

    }

    /// (define a 2)
    record Assignment(LispSymbol symbol, LispExpression expression) implements SyntacticForm {

    }

    /// Like define, but only for already defined symbols.
    record Set(LispSymbol symbol, LispExpression expression) implements SyntacticForm {}

    /// (begin
    ///   (define a 2)
    ///   (+ a 3))
    record Sequence(List<LispExpression> forms) implements SyntacticForm {

    }

    /// (lambda (a b c) (+ a b c))
    record Lambda(List<LispSymbol> arguments, LispExpression body) implements SyntacticForm {

    }

    /// (if #t 1 2)
    record Conditional(LispExpression condition, LispExpression then,
                       LispExpression otherwise) implements SyntacticForm {

    }

    /// Any list is interpreted as a function application.
    record FunctionApplication(LispExpression procedure,
                               List<LispExpression> arguments) implements SyntacticForm {

    }
}
