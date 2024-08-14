package net.fredrikmeyer.jisp;

import java.util.Objects;

public sealed interface LispLiteral extends LispExpression permits LispLiteral.NumberLiteral, LispLiteral.StringLiteral {
    final class NumberLiteral implements LispLiteral {
        private final Double value;

        NumberLiteral(Double value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        public Double value() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NumberLiteral that = (NumberLiteral) o;
            return Objects.equals(value,
                    that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }

    final class StringLiteral implements LispLiteral {
        private final String value;

        StringLiteral(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public String value() {
            return value;
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
}
