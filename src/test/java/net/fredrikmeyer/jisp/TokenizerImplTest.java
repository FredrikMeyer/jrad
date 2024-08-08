package net.fredrikmeyer.jisp;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenizerImplTest {
    @Test
    public void nullStringIsNotAllowed() {
        assertThrows(NullPointerException.class,
                () -> new TokenizerImpl().tokenize(null));
    }

    @Test
    public void canSlurpEmptyString() {
        List<Token> result = new TokenizerImpl().tokenize("");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isInstanceOf(Token.EOF.class);
    }

    @Test
    public void canSlurpWhiteSpace() {
        List<Token> result = new TokenizerImpl().tokenize("    ");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isInstanceOf(Token.EOF.class);
    }

    @Test
    public void canSlurpParens() {
        List<Token> result = new TokenizerImpl().tokenize("((  )))");

        assertThat(result).hasSize(6); // parens + EOF
        assertThat(result.stream().map(Token::getClass)).isEqualTo(List.of(Token.LeftParen.class,
                Token.LeftParen.class,
                Token.RightParen.class,
                Token.RightParen.class,
                Token.RightParen.class,
                Token.EOF.class));
    }

    @Test
    public void canSlurpSymbol() {
        List<Token> result = new TokenizerImpl().tokenize("+");

        assertThat(result).isEqualTo(List.of(new Token.Symbol("+"),
                new Token.EOF()));
    }

    @Test
    public void canSlurpString() {
        List<Token> result = new TokenizerImpl().tokenize("\"hello\"");

        assertThat(result).hasSize(2); // plus EOF
        assertThat(result.getFirst()).isEqualTo(new Token.StringLiteral("hello"));
    }

    @Test
    public void canSlurpStringAndWhiteSpace() {
        List<Token> result = new TokenizerImpl().tokenize("\"hello\" \"my darling\"");

        assertThat(result).hasSize(3); // plus EOF
        assertThat(result.getFirst()).isEqualTo(new Token.StringLiteral("hello"));
        assertThat(result.get(1)).isEqualTo(new Token.StringLiteral("my darling"));
    }

    @Test
    public void canParseInteger() {
        List<Token> result = new TokenizerImpl().tokenize("1 2 3");

        assertThat(result).hasSize(4); // plus EOF

        assertThat(result).isEqualTo(List.of(new Token.NumberLiteral(1),
                new Token.NumberLiteral(2),
                new Token.NumberLiteral(3),
                new Token.EOF()));
    }

    @Test
    public void canParseVerySimpleSExpression() {
        List<Token> result = new TokenizerImpl().tokenize("(+ 1 2)");

        assertThat(result).hasSize(6); // plus EOF

        assertThat(result).isEqualTo(List.of(
                new Token.LeftParen(),
                new Token.Symbol("+"),
                new Token.NumberLiteral(1),
                new Token.NumberLiteral(2),
                new Token.RightParen(),
                new Token.EOF()));
    }

    @Test
    public void canParseSimpleSExpression() {
        List<Token> result = new TokenizerImpl().tokenize("(+ 2 (* 3 4))");

        assertThat(result).hasSize(10); // plus EOF

        assertThat(result).isEqualTo(List.of(new Token.LeftParen(),
                new Token.Symbol("+"),
                new Token.NumberLiteral(2),
                new Token.LeftParen(),
                new Token.Symbol("*"),
                new Token.NumberLiteral(3),
                new Token.NumberLiteral(4),
                new Token.RightParen(),
                new Token.RightParen(),
                new Token.EOF()));
    }

    @Test
    public void canParseSExpressionWithStrings() {
        List<Token> result = new TokenizerImpl().tokenize("(append my-list \"hello\")");

        assertThat(result).hasSize(6); // plus EOF

        assertThat(result).isEqualTo(List.of(new Token.LeftParen(),
                new Token.Symbol("append"),
                new Token.Symbol("my-list"),
                new Token.StringLiteral("hello"),
                new Token.RightParen(),
                new Token.EOF()));
    }

    @Test
    public void canParseNestedSExpressionWithNumbersAndStrings() {
        List<Token> result = new TokenizerImpl().tokenize("(append (+ (1 - \"yo\" \"hello\"))");

        assertThat(result).hasSize(12); // plus EOF

        assertThat(result).isEqualTo(List.of(
                new Token.LeftParen(),
                new Token.Symbol("append"),
                new Token.LeftParen(),
                new Token.Symbol("+"),
                new Token.LeftParen(),
                new Token.NumberLiteral(1),
                new Token.Symbol("-"),
                new Token.StringLiteral("yo"),
                new Token.StringLiteral("hello"),
                new Token.RightParen(),
                new Token.RightParen(),
                new Token.EOF()));
    }
}