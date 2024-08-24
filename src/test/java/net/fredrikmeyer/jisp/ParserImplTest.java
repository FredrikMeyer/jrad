package net.fredrikmeyer.jisp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;
import net.fredrikmeyer.jisp.LispExpression.LispSymbol;
import net.fredrikmeyer.jisp.LispLiteral.NumberLiteral;
import net.fredrikmeyer.jisp.parser.ParserImpl;
import net.fredrikmeyer.jisp.tokenizer.Token;
import net.fredrikmeyer.jisp.tokenizer.Token.EOF;
import net.fredrikmeyer.jisp.tokenizer.TokenizerImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class ParserImplTest {

    @ParameterizedTest
    @ValueSource(strings = {"hello", "på", "deg"})
    public void parsesStringConstants(String input) {
        LispExpression res = new ParserImpl().parse(
            List.of(new Token.StringLiteral(input), new Token.EOF(1)));

        assertThat(res).isInstanceOf(LispLiteral.class);
        assertThat(res).isInstanceOf(LispLiteral.StringLiteral.class);
        assertThat(((LispLiteral.StringLiteral) res).value()).isEqualTo(input);
    }

    @Test
    public void parsesNumberLiterals() {
        LispExpression res = new ParserImpl().parse(
            List.of(new Token.NumberLiteral(2), new Token.EOF()));

        assertThat(res).isInstanceOf(LispLiteral.class);
        assertThat(res).isInstanceOf(LispLiteral.NumberLiteral.class);
        assertThat(((LispLiteral.NumberLiteral) res).value()).isEqualTo(2);
    }

    @Test
    public void parseSimpleExpression() {
        LispExpression res = new ParserImpl().parse(
            List.of(new Token.LeftParen(),
                new Token.Symbol("+"),
                new Token.NumberLiteral(2),
                new Token.NumberLiteral(3),
                new Token.RightParen(),
                new Token.EOF()));

        assertThat(res).isInstanceOf(LispList.class);
        assertThat(res).isEqualTo(new LispList(
            List.of(new LispSymbol("+"), new LispLiteral.NumberLiteral(2.0),
                new LispLiteral.NumberLiteral(3.0))));
    }

    @Test
    public void parseComplexExpression() {
        // (+ (* 2 3) 3)
        LispExpression res = new ParserImpl().parse(
            List.of(new Token.LeftParen(), new Token.Symbol("+"), new Token.LeftParen(),
                new Token.Symbol("*"), new Token.NumberLiteral(2), new Token.NumberLiteral(3),
                new Token.RightParen(), new Token.NumberLiteral(3), new Token.RightParen(),
                new Token.EOF()));

        assertThat(res).isInstanceOf(LispList.class);
        assertThat(res).isEqualTo(new LispList(List.of(new LispSymbol("+"), new LispList(
            List.of(new LispSymbol("*"), new LispLiteral.NumberLiteral(2.0),
                new LispLiteral.NumberLiteral(3.0))), new LispLiteral.NumberLiteral(3.0))));
    }

    @ParameterizedTest
    @MethodSource("expressions")
    public void parse(List<Token> input, LispExpression expected) {
        LispExpression res = new ParserImpl().parse(input);

        assertThat(res).isEqualTo(expected);
    }

    private static Stream<Arguments> expressions() {
        TokenizerImpl tokenizer = new TokenizerImpl();
        return Stream.of(
            Arguments.arguments(tokenizer.tokenize("(+ (* 2 3))"),
                new LispList(
                    List.of(new LispSymbol("+"), new LispList(
                        List.of(new LispSymbol("*"), new LispLiteral.NumberLiteral(2.0),
                            new LispLiteral.NumberLiteral(3.0)))))),
            Arguments.arguments(tokenizer.tokenize("(lambda (x) (+ 1 x))"),
                new LispList(new LispSymbol("lambda"), new LispList(new LispSymbol("x")),
                    new LispList(new LispSymbol("+"), new LispLiteral.NumberLiteral(1.0),
                        new LispSymbol("x")))),
            Arguments.arguments(tokenizer.tokenize("(())"), new LispList((new LispList()))));
    }

    @Test
    public void exceptionWhenNoTokensPassed() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class,
            () -> new ParserImpl().parse(List.of(new EOF())));

        assertThat(runtimeException).hasMessage("No tokens parsed.");
    }

    @Test
    public void unbalancedParensWithStrings() {
        List<Token> result = new TokenizerImpl().tokenize("(\"hei\"))");

        RuntimeException runtimeException = assertThrows(RuntimeException.class,
            () -> new ParserImpl().parse(result));

        assertThat(runtimeException).hasMessageContaining("Mismatched parentheses.");
    }

    @Test
    void canParseQuote() {
        List<Token> result = new TokenizerImpl().tokenize("'(1 2)");

        LispExpression res = new ParserImpl().parse(result);

        assertThat(res).isInstanceOf(LispList.class);
        assertThat(res).isEqualTo(new LispList(
            List.of(
                new LispSymbol("quote"),
                new LispList(new NumberLiteral(1.0), new NumberLiteral(2.0)))));
    }

    @Test
    void parseNestedQuote() {
        List<Token> result = new TokenizerImpl().tokenize("(begin 1 (begin 2 '(1 2)))");

        LispExpression res = new ParserImpl().parse(result);

        assertThat(res).isInstanceOf(LispList.class);
        assertThat(res).isEqualTo(new LispList(
            List.of(
                new LispSymbol("begin"),
                new LispLiteral.NumberLiteral(1.0),
                new LispList(new LispSymbol("begin"),
                    new NumberLiteral(2.0),
                    new LispList(new LispSymbol("quote"),
                        new LispList(new NumberLiteral(1.0), new NumberLiteral(2.0)))))));
    }
}