package net.fredrikmeyer.jisp.tokenizer;

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

    record Symbol(String value, int position) implements Token {

        public Symbol(String value) {
            this(value, 0);
        }
    }

    record StringLiteral(String value, int position) implements Token {

        public StringLiteral(String value) {
            this(value, 0);
        }
    }

    record NumberLiteral(double value, int position) implements Token {

        public NumberLiteral(double value) {
            this(value, 0);
        }
    }

    record BooleanLiteral(boolean value, int position) implements Token {

    }

    record Quote(int position) implements Token {

    }

    record EOF(int position) implements Token {

        public EOF() {
            this(0);
        }
    }
}