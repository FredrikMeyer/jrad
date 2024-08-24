package net.fredrikmeyer.jisp.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.fredrikmeyer.jisp.tokenizer.Token.BooleanLiteral;
import net.fredrikmeyer.jisp.tokenizer.Token.Quote;
import org.intellij.lang.annotations.Language;

public class TokenizerImpl implements Tokenizer {

    private int position = 0;
    private String input;

    @Override
    public List<Token> tokenize(@Language("scheme") String input) {
        position = 0;
        Objects.requireNonNull(input);

        List<Token> tokens = new ArrayList<>();
        this.input = input;
        while (position < input.length()) {
            if (Character.isWhitespace(current())) {
                slurpWhitespace();
            } else if (current() == '(') {
                tokens.add(new Token.LeftParen(position));
                advance();
            } else if (current() == ')') {
                tokens.add(new Token.RightParen(position));
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
            } else if (current() == '\'') {
                Quote quote = slurpQuote();
                tokens.add(quote);
            } else if (current() == '#') {
                BooleanLiteral booleanLiteral = slurpBoolean();
                tokens.add(booleanLiteral);
            } else {
                throw new IllegalArgumentException("Unexpected character: " + current() + ". Position: " + position + ".");
            }
        }
        tokens.add(new Token.EOF(position));
        return tokens;
    }

    private static boolean isAllowedCharacterInSymbol(char currentChar) {
        return Character.isAlphabetic(currentChar)
            || currentChar == '_'
            || currentChar == '-'
            || currentChar == '+'
            || currentChar == '*'
            || currentChar == '!'
            || currentChar == '=' || currentChar == '<' || currentChar == '>';
    }

    private void advance() {
        position++;
    }

    private void retreat() {
        position--;
    }

    private char current() {
        assert position < input.length();
        return input.charAt(position);
    }

    private Token.NumberLiteral slurpNumber() {
        StringBuilder value = new StringBuilder();
        int initPosition = position;
        while ((position) < input.length() && Character.isDigit(current())) {
            value.append(current());
            advance();
        }

        return new Token.NumberLiteral(Double.parseDouble(value.toString()), initPosition);
    }

    private Token.StringLiteral slurpString() {
        StringBuilder value = new StringBuilder();
        advance();
        int initPosition = position;
        while ((position) < input.length() && current() != '"') {
            value.append(current());
            advance();
        }

        return new Token.StringLiteral(value.toString(), initPosition);
    }

    private Token.Symbol slurpSymbol() {
        StringBuilder value = new StringBuilder();
        int initPosition = position;
        while (position < input.length() && isAllowedCharacterInSymbol(current())) {
            value.append(current());
            advance();
        }
        retreat();

        return new Token.Symbol(value.toString(), initPosition);
    }

    private Token.Quote slurpQuote() {
        int initPosition = position;
        advance();
        return new Token.Quote(initPosition);
    }

    private Token.BooleanLiteral slurpBoolean() {
        int initPosition = position;
        advance();
        if (current() == 't') {
            BooleanLiteral booleanLiteral = new BooleanLiteral(true, initPosition);
            advance();
            return booleanLiteral;
        } else if (current() == 'f') {
            BooleanLiteral booleanLiteral = new BooleanLiteral(false, initPosition);
            advance();
            return booleanLiteral;
        } else {
            throw new IllegalArgumentException("Unexpected character: " + current() + ". Position: " + position + ".");
        }
    }

    private void slurpWhitespace() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }
}
