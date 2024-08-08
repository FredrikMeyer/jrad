package net.fredrikmeyer.jisp;

import java.util.List;
import java.util.stream.Collectors;

public sealed interface LispExpression permits LispLiteral, LispVariable, LispList {
}


sealed class LispLiteral implements LispExpression permits LispLiteral.NumberLiteral, LispLiteral.StringLiteral {
    non-sealed static class NumberLiteral extends LispLiteral {
        private final Double value;

        NumberLiteral(Double value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

    non-sealed static class StringLiteral extends LispLiteral {
        private final String value;

        StringLiteral(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}

non-sealed class LispVariable implements LispExpression {
    private final String name;

    public LispVariable(String name) {
        this.name = name;
    }

    public String value() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

// Define a class for lists of expressions (e.g., function calls, nested lists)
non-sealed class LispList implements LispExpression {
    private final List<LispExpression> elements;

    public LispList(List<LispExpression> elements) {
        this.elements = elements;
    }

    public List<LispExpression> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        String collect = elements.stream()
                .map(LispExpression::toString)
                .collect(Collectors.joining(" "));
        return "(" +
               collect +
               ')';
    }
}
