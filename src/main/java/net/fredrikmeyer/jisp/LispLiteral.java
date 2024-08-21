package net.fredrikmeyer.jisp;

import net.fredrikmeyer.jisp.LispLiteral.BoolValue;
import net.fredrikmeyer.jisp.LispLiteral.NumberLiteral;
import net.fredrikmeyer.jisp.LispLiteral.StringLiteral;

public sealed interface LispLiteral extends LispExpression permits BoolValue, NumberLiteral,
    StringLiteral {

    record NumberLiteral(Double value) implements LispLiteral {

        @Override
        public String toString() {
            return value.toString();
        }
    }

    record StringLiteral(String value) implements LispLiteral {

        @Override
        public String toString() {
            return "\"" + value + "\"";
        }

    }

    record BoolValue(boolean value) implements LispLiteral {

        @Override
        public String toString() {
            return value ? "#t" : "#f";
        }
    }
}