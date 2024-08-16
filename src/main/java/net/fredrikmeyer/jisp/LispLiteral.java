package net.fredrikmeyer.jisp;

public sealed interface LispLiteral extends LispExpression permits LispLiteral.NumberLiteral,
    LispLiteral.StringLiteral {

    record NumberLiteral(Double value) implements LispLiteral {

        @Override
        public String toString() {
            return value.toString();
        }
    }

    record StringLiteral(String value) implements LispLiteral {

        @Override
        public String toString() {
            return value;
        }

    }
}