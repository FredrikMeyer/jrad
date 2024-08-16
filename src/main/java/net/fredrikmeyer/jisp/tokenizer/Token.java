package net.fredrikmeyer.jisp.tokenizer;

import java.util.Objects;

public sealed interface Token {

    int position();

    record LeftParen(int position) implements Token {

        public LeftParen() {
            this(0);
        }
    }

    record RightParen(int position) implements Token {

        public RightParen() {
            this(0);
        }

        @Override
        public int position() {
            return position;
        }
    }

    final class Symbol implements Token {

        private final String value;
        private final int position;

        public Symbol(String value, int position) {
            this.value = value;
            this.position = position;
        }

        public Symbol(String value) {
            this.value = value;
            this.position = 0;
        }

        public String value() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Symbol symbol = (Symbol) o;
            return Objects.equals(value,
                symbol.value);
        }

        @Override
        public String toString() {
            return "Symbol{" +
                "value='" + value + '\'' +
                ", position=" + position +
                '}';
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public int position() {
            return position;
        }
    }

    final class StringLiteral implements Token {

        private final String value;
        private final int position;

        public StringLiteral(String value, int position) {
            this.value = value;
            this.position = position;
        }

        public StringLiteral(String value) {
            this.value = value;
            this.position = 0;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return "StringLiteral{" +
                "value='" + value + '\'' +
                ", position=" + position +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            StringLiteral that = (StringLiteral) o;
            return Objects.equals(value,
                that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public int position() {
            return position;
        }
    }

    final class NumberLiteral implements Token {

        private final double value;
        private final int position;

        public NumberLiteral(double value, int position) {
            this.value = value;
            this.position = position;
        }

        public NumberLiteral(double value) {
            this.value = value;
            this.position = 0;
        }

        public double value() {
            return value;
        }

        @Override
        public String toString() {
            return "NumberLiteral{" +
                "value=" + value +
                ", position=" + position +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            NumberLiteral that = (NumberLiteral) o;
            return Double.compare(value,
                that.value) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public int position() {
            return position;
        }
    }

    final class EOF implements Token {

        private static final String value = "EOF";
        private final int position;

        public EOF(int position) {
            this.position = position;
        }

        public EOF() {
            this.position = 0;
        }

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
            return "EOF{" +
                "position=" + position +
                '}';
        }

        @Override
        public int position() {
            return position;
        }
    }
}