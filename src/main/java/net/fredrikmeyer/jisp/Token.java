package net.fredrikmeyer.jisp;

import java.util.Objects;

public sealed interface Token {
    final class LeftParen implements Token {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof LeftParen;
        }

        @Override
        public String toString() {
            return "LeftParen{}";
        }
    }

    final class RightParen implements Token {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof RightParen;
        }

        @Override
        public String toString() {
            return "RightParen{}";
        }
    }

    final class Symbol implements Token {
        private final String value;

        Symbol(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return "Symbol{" + "value='" + value + '\'' + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Symbol symbol = (Symbol) o;
            return Objects.equals(value,
                    symbol.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }

    final class StringLiteral implements Token {
        private final String value;

        public StringLiteral(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return "StringLiteral{" + "value='" + value + '\'' + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StringLiteral that = (StringLiteral) o;
            return Objects.equals(value,
                    that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }

    final class NumberLiteral implements Token {
        private final double value;

        public NumberLiteral(double value) {
            this.value = value;
        }

        public double value() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NumberLiteral that = (NumberLiteral) o;
            return Double.compare(value,
                    that.value) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return "NumberLiteral{" + "value=" + value + '}';
        }
    }

    final class EOF implements Token {
        private static final String value = "EOF";

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EOF;
        }

        @Override
        public String toString() {
            return "EOF{}";
        }
    }
}