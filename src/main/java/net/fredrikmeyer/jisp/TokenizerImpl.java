package net.fredrikmeyer.jisp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TokenizerImpl implements Tokenizer {
    private int position = 0;
    private String input;

    public TokenizerImpl() {
    }

    @Override
    public List<Token> tokenize(String input) {
        Objects.requireNonNull(input);

        List<Token> tokens = new ArrayList<>();
        this.input = input;
        while (position < input.length()) {
            if (Character.isWhitespace(current())) {
                slurpWhitespace();
            } else if (current() == '(') {
                tokens.add(new Token.LeftParen());
                advance();
            } else if (current() == ')') {
                tokens.add(new Token.RightParen());
                advance();
            } else if (isAllowedCharacterInSymbol(current())) {
                Token.Symbol symbol = slurpSymbol();
                tokens.add(symbol);
                advance();
            } else if (current() == '"') {
                Token.StringLiteral stringLiteral = slurpString();
                tokens.add(stringLiteral);
                advance();
            } else if (Character.isDigit(current())) {
                Token.NumberLiteral numberLiteral = slurpNumber();
                tokens.add(numberLiteral);
            } else {
                throw new IllegalArgumentException("Unexpected character: " + current());
            }
        }
        tokens.add(new Token.EOF());
        return tokens;
    }

    private static boolean isAllowedCharacterInSymbol(char currentChar) {
        return Character.isAlphabetic(currentChar)
               || currentChar == '_'
               || currentChar == '-'
               || currentChar == '+'
               || currentChar == '*';
    }

    private void advance() {
        position++;
    }

    private char current() {
        assert position < input.length();
        return input.charAt(position);
    }

    private char peek() {
        assert position < input.length();
        return input.charAt(position++);
    }

    private Token.NumberLiteral slurpNumber() {
        StringBuilder value = new StringBuilder();
        while ((position) < input.length() && Character.isDigit(current())) {
            value.append(current());
            advance();
        }

        return new Token.NumberLiteral(Double.parseDouble(value.toString()));
    }

    private Token.StringLiteral slurpString() {
        StringBuilder value = new StringBuilder();
        advance();
        while ((position) < input.length() && current() != '"') {
            value.append(current());
            advance();
        }

        return new Token.StringLiteral(value.toString());
    }

    private Token.Symbol slurpSymbol() {
        StringBuilder value = new StringBuilder();
        while (position < input.length() && isAllowedCharacterInSymbol(current())) {
            value.append(current());
            advance();
        }

        return new Token.Symbol(value.toString());
    }

    private void slurpWhitespace() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }
}
