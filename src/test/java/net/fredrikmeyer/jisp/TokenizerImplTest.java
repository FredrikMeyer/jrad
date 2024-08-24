package net.fredrikmeyer.jisp;

import net.fredrikmeyer.jisp.tokenizer.Token;
import net.fredrikmeyer.jisp.tokenizer.Token.LeftParen;
import net.fredrikmeyer.jisp.tokenizer.Token.RightParen;
import net.fredrikmeyer.jisp.tokenizer.TokenizerImpl;
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
            new Token.EOF(1)));
    }

    @Test
    public void canSlurpString() {
        List<Token> result = new TokenizerImpl().tokenize("\"hello\"");

        assertThat(result).hasSize(2); // plus EOF
        assertThat(result.getFirst()).isEqualTo(new Token.StringLiteral("hello", 1));
    }

    @Test
    public void canSlurpStringAndWhiteSpace() {
        List<Token> result = new TokenizerImpl().tokenize("\"hello\" \"my darling\"");

        assertThat(result).hasSize(3); // plus EOF
        assertThat(result.getFirst()).isEqualTo(new Token.StringLiteral("hello", 1));
        assertThat(result.get(1)).isEqualTo(new Token.StringLiteral("my darling", 9));
    }

    @Test
    public void canParseInteger() {
        List<Token> result = new TokenizerImpl().tokenize("1 2 3");

        assertThat(result).hasSize(4); // plus EOF

        assertThat(result).containsExactly(
            new Token.NumberLiteral(1, 0),
            new Token.NumberLiteral(2, 2),
            new Token.NumberLiteral(3, 4),
            new Token.EOF(5)
        );
    }

    @Test
    public void canParseVerySimpleSExpression() {
        List<Token> result = new TokenizerImpl().tokenize("(+ 1 2)");

        assertThat(result).hasSize(6); // plus EOF

        assertThat(result).containsExactly(
            new Token.LeftParen(0),
            new Token.Symbol("+", 1),
            new Token.NumberLiteral(1.0, 3),
            new Token.NumberLiteral(2.0, 5),
            new Token.RightParen(6),
            new Token.EOF(7));
    }

    @Test
    public void canParseSimpleSExpression() {
        List<Token> result = new TokenizerImpl().tokenize("(+ 2 (* 3 4))");

        assertThat(result).hasSize(10); // plus EOF

        assertThat(result).containsExactly(
            new Token.LeftParen(0),
            new Token.Symbol("+", 1),
            new Token.NumberLiteral(2, 3),
            new Token.LeftParen(5),
            new Token.Symbol("*", 6),
            new Token.NumberLiteral(3, 8),
            new Token.NumberLiteral(4, 10),
            new Token.RightParen(11),
            new Token.RightParen(12),
            new Token.EOF(13));
    }

    @Test
    public void canParseSExpressionWithStrings() {
        List<Token> result = new TokenizerImpl().tokenize("(append my-list \"hello\")");

        assertThat(result).hasSize(6); // plus EOF

        assertThat(result).containsExactly(
            new Token.LeftParen(0),
            new Token.Symbol("append", 1),
            new Token.Symbol("my-list", 8),
            new Token.StringLiteral("hello", 17),
            new Token.RightParen(23),
            new Token.EOF(24));
    }

    @Test
    public void canParseNestedSExpressionWithNumbersAndStrings() {
        List<Token> result = new TokenizerImpl().tokenize("(append (+ (1 - \"yo\" \"hello\"))");

        assertThat(result).hasSize(12); // plus EOF

        assertThat(result).containsExactly(
            new Token.LeftParen(0),
            new Token.Symbol("append", 1),
            new Token.LeftParen(8),
            new Token.Symbol("+", 9),
            new Token.LeftParen(11),
            new Token.NumberLiteral(1, 12),
            new Token.Symbol("-", 14),
            new Token.StringLiteral("yo", 17),
            new Token.StringLiteral("hello", 22),
            new Token.RightParen(28),
            new Token.RightParen(29),
            new Token.EOF(30));
    }

    @Test
    public void canParseLambda() {
        List<Token> result = new TokenizerImpl().tokenize("(lambda (x) (+ x 2))");

        assertThat(result).containsExactly(
            new Token.LeftParen(0),
            new Token.Symbol("lambda", 1),
            new Token.LeftParen(8),
            new Token.Symbol("x", 9),
            new Token.RightParen(10),
            new Token.LeftParen(12),
            new Token.Symbol("+", 13),
            new Token.Symbol("x", 15),
            new Token.NumberLiteral(2, 17),
            new Token.RightParen(18),
            new Token.RightParen(19),
            new Token.EOF(20)
        );
    }

    @Test
    public void canParseNestedParens() {
        List<Token> result = new TokenizerImpl().tokenize("(())");

        assertThat(result).isEqualTo(List.of(
            new LeftParen(0),
            new LeftParen(1),
            new RightParen(2),
            new RightParen(3),
            new Token.EOF(4))
        );
    }

    @Test
    public void canTokenizeAssignment() {
        List<Token> result = new TokenizerImpl().tokenize("(set! a 2");

        assertThat(result).isEqualTo(List.of(
            new Token.LeftParen(0),
            new Token.Symbol("set!", 1),
            new Token.Symbol("a", 6),
            new Token.NumberLiteral(2, 8),
            new Token.EOF(9)
        ));
    }

    @Test
    public void canParseEquals() {
        List<Token> result = new TokenizerImpl().tokenize("(= 1 2 3)");

        assertThat(result).containsExactly(
            new Token.LeftParen(0),
            new Token.Symbol("=", 1),
            new Token.NumberLiteral(1, 3),
            new Token.NumberLiteral(2, 5),
            new Token.NumberLiteral(3, 7),
            new Token.RightParen(8),
            new Token.EOF(9)
        );
    }

    @Test
    public void canParseQuote() {
        List<Token> result = new TokenizerImpl().tokenize("'(1 2)");

        assertThat(result).containsExactly(
            new Token.Quote(0),
            new Token.LeftParen(1),
            new Token.NumberLiteral(1, 2),
            new Token.NumberLiteral(2, 4),
            new Token.RightParen(5),
            new Token.EOF(6)
        );
    }

    @Test
    public void canParseBoolLiteral() {
        List<Token> result = new TokenizerImpl().tokenize("#t #f");

        assertThat(result).containsExactly(
            new Token.BooleanLiteral(true, 0),
            new Token.BooleanLiteral(false, 3),
            new Token.EOF(5)
        );
    }
}