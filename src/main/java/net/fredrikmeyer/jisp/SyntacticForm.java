package net.fredrikmeyer.jisp;

import java.util.List;
import net.fredrikmeyer.jisp.LispExpression.LispSymbol;

sealed interface SyntacticForm {

    record SelfEvaluating(LispLiteral literal) implements SyntacticForm {

    }

    record Variable(LispSymbol s) implements SyntacticForm {

    }

    record Quotation(LispExpression expression) implements SyntacticForm {

    }

    record Assignment(LispSymbol symbol, LispExpression expression) implements SyntacticForm {

    }

    record Sequence(List<LispExpression> forms) implements SyntacticForm {

    }

    record Lambda(List<LispSymbol> arguments, LispExpression body) implements SyntacticForm {

    }

    record Conditional(LispExpression condition, LispExpression then,
                       LispExpression otherwise) implements SyntacticForm {

    }

    record FunctionApplication(LispExpression procedure,
                               List<LispExpression> arguments) implements SyntacticForm {

    }
}
