package net.fredrikmeyer.jisp;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ParserImplTest {
    @Test
    public void parsesConstants() {
        LispExpression res = new ParserImpl().parse(List.of(
                new Token.StringLiteral("hello"),
                new Token.EOF()));

        assertThat(res).isInstanceOf(LispLiteral.class);
    }
}