package net.fredrikmeyer.jisp;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class EndToEndTests {

    @ParameterizedTest
    @MethodSource("expressions")
    public void evaluateSimpleSExpression(String input, LispValue expected) {
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
            Arguments.of("(+ (* 2 3))", new NumberValue(6)),
            Arguments.of("(+ 2 (* 2 3))", new NumberValue(8)),
            Arguments.of("(lambda (x) (+ 1 x))", new NumberValue(2))
        );
    }
}
