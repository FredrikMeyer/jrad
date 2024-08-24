package net.fredrikmeyer.jisp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import net.fredrikmeyer.jisp.LispExpression.LispSymbol;
import org.junit.jupiter.api.Test;

class LispExpressionTest {

    @Test
    public void stringifiesNicely() {
        String expression = new LispList(List.of(new LispSymbol("+"),
                new LispLiteral.NumberLiteral(1.),
                new LispLiteral.NumberLiteral(2.))).toString();

        assertThat(expression).isEqualTo("(+ 1.0 2.0)");
    }

    @Test
    public void stringsStringifiesNicely() {
        String expression = new LispList(List.of(new LispSymbol("+"),
            new LispLiteral.StringLiteral("hei"),
            new LispLiteral.NumberLiteral(2.))).toString();

        assertThat(expression).isEqualTo("(+ \"hei\" 2.0)");
    }
}