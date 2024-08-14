package net.fredrikmeyer.jisp;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LispExpressionTest {

    @Test
    public void stringifiesNicely() {
        String expression = new LispList(List.of(new LispSymbol("+"),
                new LispLiteral.NumberLiteral(1.),
                new LispLiteral.NumberLiteral(2.))).toString();

        assertThat(expression).isEqualTo("(+ 1.0 2.0)");
    }
}