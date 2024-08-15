package net.fredrikmeyer.jisp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ParserImplTest {

    @ParameterizedTest
    @ValueSource(strings = {"hello", "på", "deg"})
    public void parsesStringConstants(String input) {
        LispExpression res = new ParserImpl().parse(
            List.of(new Token.StringLiteral(input), new Token.EOF()));

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
            List.of(new Token.LeftParen(), new Token.Symbol("+"), new Token.NumberLiteral(2),
                new Token.NumberLiteral(3), new Token.RightParen(), new Token.EOF()));

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

        System.out.println(input);
        System.out.println("EXP " + expected);
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
    public void fff() {

    }
}