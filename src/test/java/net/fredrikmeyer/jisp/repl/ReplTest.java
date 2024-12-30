package net.fredrikmeyer.jisp.repl;

import static org.assertj.core.api.Assertions.assertThat;

import net.fredrikmeyer.jisp.repl.ReplResult.StringValue;
import org.junit.jupiter.api.Test;

class ReplTest {

    @Test
    void testAddTwoNumber() {
        Repl repl = new Repl();

        var answer = repl.write("(+ 1 2)");

        assertThat(answer).isInstanceOf(StringValue.class);
        assertThat(((StringValue) answer).value()).isEqualTo("3.0");
    }
}