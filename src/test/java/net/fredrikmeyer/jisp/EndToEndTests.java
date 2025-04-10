package net.fredrikmeyer.jisp;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Stream;
import net.fredrikmeyer.jisp.LispExpression.LispSymbol;
import net.fredrikmeyer.jisp.LispExpression.Ok;
import net.fredrikmeyer.jisp.LispLiteral.BoolValue;
import net.fredrikmeyer.jisp.LispLiteral.NumberLiteral;
import net.fredrikmeyer.jisp.environment.Environment;
import net.fredrikmeyer.jisp.environment.StandardEnvironment;
import net.fredrikmeyer.jisp.evaluator.EvalApply;
import net.fredrikmeyer.jisp.evaluator.StandardEvalApply;
import net.fredrikmeyer.jisp.parser.Parser;
import net.fredrikmeyer.jisp.parser.ParserImpl;
import net.fredrikmeyer.jisp.tokenizer.Token;
import net.fredrikmeyer.jisp.tokenizer.Tokenizer;
import net.fredrikmeyer.jisp.tokenizer.TokenizerImpl;
import org.assertj.core.data.Offset;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class EndToEndTests {

    @ParameterizedTest
    @MethodSource("expressions")
    public void evaluateSimpleSExpression(String input, LispExpression expected) {
        Tokenizer tokenizer = new TokenizerImpl();
        Parser parser = new ParserImpl();
        EvalApply evalApply = new StandardEvalApply();

        List<Token> tokens = tokenizer.tokenize(input);
        LispExpression parsed = parser.parse(tokens);

        Environment environment = new StandardEnvironment();

        var res = evalApply.eval(parsed, environment);

        assertThat(res).isEqualTo(expected);
    }

    private static Stream<Arguments> expressions() {
        return Stream.of(
            Arguments.of("(+ (* 2 3))", new NumberLiteral(6.0)),
            Arguments.of("(+ 2 (* 2 3))", new NumberLiteral(8.0)),
            Arguments.of("((lambda (x) (+ 1 x)) 1)", new NumberLiteral(2.)),
            Arguments.of("(define f (lambda (x) (+ x 1)))", new Ok()),
            Arguments.of("(begin (define f (lambda (x) (+ x 1))) (f 2))", new NumberLiteral(3.0)),
            Arguments.of("(< 5 4 3)", new BoolValue(false)),
            Arguments.of("(< 1 2 3)", new BoolValue(true)),
            Arguments.of("(- 5 2)", new NumberLiteral(3.0)),
            Arguments.of("(- 5 3 1)", new NumberLiteral(1.0)),
            Arguments.of("(* -5 -5)", new NumberLiteral(25.0)),
            Arguments.of("(if (= 3 3) 1 2)", new NumberLiteral(1.0)),
            Arguments.of("(if (= 3 4) 1 2)", new NumberLiteral(2.0)),
            Arguments.of("(< 5 6 3 2)", new BoolValue(false)),
            Arguments.of("(< 9999 2)", new BoolValue(false)),
            Arguments.of("(/ 5 2)", new NumberLiteral(2.5)),
            Arguments.of("(% 10 3)", new NumberLiteral(1.0)),
            Arguments.of("(% 10 2)", new NumberLiteral(0.0)),
            Arguments.of("(% -10 3)", new NumberLiteral(-1.0)),
            Arguments.of("(% 10.5 3.2)", new NumberLiteral(10.5 % 3.2)),
            Arguments.of("(if #t 1 2)", new NumberLiteral(1.0)),
            Arguments.of("(begin (define a 2) (set! a 3) a)", new NumberLiteral(3.0)),
            Arguments.of("(begin (define a 2) (set! a (+ a 1)) a)", new NumberLiteral(3.0)),
            Arguments.of("""
                (begin (define a 2)
                       ((lambda (a) a) 3))""", new NumberLiteral(3.0)),
            Arguments.of("(begin 1 '(1 2))",
                new LispList(new NumberLiteral(1.0), new NumberLiteral(2.0))),
            Arguments.of("'(1 2)", new LispList(new NumberLiteral(1.0), new NumberLiteral(2.0))),
            Arguments.of("""
                (begin (define f (lambda (n)
                                (if (= n 0)
                                     1
                                     (if (= n 1)
                                         1
                                         (+ (f (- n 1)) (f (- n 2)))))))
                       (f 10))
                """, new NumberLiteral(89.0)),
            Arguments.of("""
                (begin
                  (define is-even (lambda (n)
                                  (if (= n 0) #t (is-odd (- n 1)))))
                  (define is-odd (lambda (n)
                                 (if (= n 0) #f (is-even (- n 1)))))
                  (list (is-even 20) (is-odd 20)))
                """, new LispList(new BoolValue(true), new BoolValue(false))),
            // Test map function with a built-in procedure
            Arguments.of("""
                (map abs '(-1 2 -3 4 -5))
                """, new LispList(
                    new NumberLiteral(1.0),
                    new NumberLiteral(2.0),
                    new NumberLiteral(3.0),
                    new NumberLiteral(4.0),
                    new NumberLiteral(5.0)
                )),
            // Test map function with a lambda
            Arguments.of("""
                (map (lambda (x) (* x x)) '(1 2 3 4 5))
                """, new LispList(
                    new NumberLiteral(1.0),
                    new NumberLiteral(4.0),
                    new NumberLiteral(9.0),
                    new NumberLiteral(16.0),
                    new NumberLiteral(25.0)
                )),
            // Test filter function with a built-in predicate
            Arguments.of("""
                (begin
                  (define positive? (lambda (x) (< 0 x)))
                  (filter positive? '(-2 -1 0 1 2)))
                """, new LispList(
                    new NumberLiteral(1.0),
                    new NumberLiteral(2.0)
                )),
            // Test filter function with a lambda predicate for even numbers using modulo
            Arguments.of("""
                (begin
                  (define is-even? (lambda (x)
                    (= (% x 2) 0)))
                  (filter is-even? '(1 2 3 4 5 6)))
                """, new LispList(
                    new NumberLiteral(2.0),
                    new NumberLiteral(4.0),
                    new NumberLiteral(6.0)
                ))
        );
    }

    @Test
    public void equalsImplementation() {
        Tokenizer tokenizer = new TokenizerImpl();
        Parser parser = new ParserImpl();
        EvalApply evalApply = new StandardEvalApply();

        List<Token> tokens = tokenizer.tokenize("(= 1 1)");
        var parsed = parser.parse(tokens);

        LispExpression eval = evalApply.eval(parsed, new StandardEnvironment());

        assertThat(eval).isEqualTo(new BoolValue(true));
    }

    @Test
    void squareRootTest() throws IOException {
        String s = readFromFile("sqrt.scm");

        System.out.println(s);

        Tokenizer tokenizer = new TokenizerImpl();
        Parser parser = new ParserImpl();
        EvalApply evalApply = new StandardEvalApply();

        List<Token> tokens = tokenizer.tokenize(s);

        LispExpression parsed = parser.parse(tokens);

        Environment environment = new StandardEnvironment();

        LispExpression res = evalApply.eval(parsed, environment);

        assertThat(((NumberLiteral) res).value())
            .isCloseTo(1.4142224,
                Offset.offset(0.001));
    }

    @Test
    void makeAccount() throws IOException {
        String s = readFromFile("make-account.scm");

        Tokenizer tokenizer = new TokenizerImpl();
        Parser parser = new ParserImpl();
        EvalApply evalApply = new StandardEvalApply();

        System.out.println(s);
        List<Token> tokens = tokenizer.tokenize(s);

        LispExpression parsed = parser.parse(tokens);

        Environment environment = new StandardEnvironment();

        LispExpression res = evalApply.eval(parsed, environment);

        assertThat(((NumberLiteral) res).value())
            .isCloseTo(80,
                Offset.offset(0.001));
    }

    @Test
    void shouldGiveStackOverFlow() {
        Tokenizer tokenizer = new TokenizerImpl();
        Parser parser = new ParserImpl();
        EvalApply evalApply = new StandardEvalApply();

        LispExpression expression = parser.parse(
            tokenizer.tokenize("((lambda (X) (X X)) (lambda (X) (X X)))"));

        assertThrows(StackOverflowError.class, () -> evalApply.eval(expression, new StandardEnvironment()));
    }

    private @NotNull String readFromFile(String fileName) throws IOException {
        ClassLoader classloader = getClass().getClassLoader();
        InputStream inputStream = classloader.getResourceAsStream("files/" + fileName);

        StringBuilder b = new StringBuilder();
        b.append("(begin ");
        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            // Read line by line until the end of the stream
            while ((line = reader.readLine()) != null) {
                b.append(line).append("\n");
            }
            reader.close();
        } else {
            System.out.println("File not found");
        }
        b.append(")");

        return b.toString();
    }
}
