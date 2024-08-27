package net.fredrikmeyer.jisp;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Stream;
import net.fredrikmeyer.jisp.LispExpression.LispSymbol;
import net.fredrikmeyer.jisp.LispLiteral.BoolValue;
import net.fredrikmeyer.jisp.LispLiteral.NumberLiteral;
import net.fredrikmeyer.jisp.environment.Environment;
import net.fredrikmeyer.jisp.environment.StandardEnvironment;
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
        IEvalApply evalApply = new EvalApplyImpl();

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
            Arguments.of("(define f (lambda (x) (+ x 1)))", new LispSymbol("ok")),
            Arguments.of("(begin (define f (lambda (x) (+ x 1))) (f 2))", new NumberLiteral(3.0)),
            Arguments.of("(< 5 4 3)", new BoolValue(false)),
            Arguments.of("(< 1 2 3)", new BoolValue(true)),
            Arguments.of("(- 5 2)", new NumberLiteral(3.0)),
            Arguments.of("(- 5 3 1)", new NumberLiteral(1.0)),
            Arguments.of("(if (= 3 3) 1 2)", new NumberLiteral(1.0)),
            Arguments.of("(if (= 3 4) 1 2)", new NumberLiteral(2.0)),
            Arguments.of("(< 5 6 3 2)", new BoolValue(false)),
            Arguments.of("(< 9999 2)", new BoolValue(false)),
            Arguments.of("(/ 5 2)", new NumberLiteral(2.5)),
            Arguments.of("(if #t 1 2)", new NumberLiteral(1.0)),
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
                """, new LispList(new BoolValue(true), new BoolValue(false)))
        );
    }

    @Test
    public void equalsImplementation() {
        Tokenizer tokenizer = new TokenizerImpl();
        Parser parser = new ParserImpl();
        IEvalApply evalApply = new EvalApplyImpl();

        List<Token> tokens = tokenizer.tokenize("(= 1 1)");
        var parsed = parser.parse(tokens);

        LispExpression eval = evalApply.eval(parsed, new StandardEnvironment());

        assertThat(eval).isEqualTo(new BoolValue(true));
    }

    @Test
    void expressionsFromResources() throws IOException {
        String s = readFromFile("test.scm");

        System.out.println(s);

        Tokenizer tokenizer = new TokenizerImpl();
        Parser parser = new ParserImpl();
        IEvalApply evalApply = new EvalApplyImpl();

        List<Token> tokens = tokenizer.tokenize(s);

        LispExpression parsed = parser.parse(tokens);

        Environment environment = new StandardEnvironment();

        LispExpression res = evalApply.eval(parsed, environment);

        assertThat(((NumberLiteral) res).value())
            .isCloseTo(1.4142224,
                Offset.offset(0.001));
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

        String s = b.toString();
        return s;
    }
}
