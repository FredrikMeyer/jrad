package net.fredrikmeyer.jisp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import net.fredrikmeyer.jisp.LispLiteral.NumberLiteral;
import net.fredrikmeyer.jisp.environment.Environment;
import net.fredrikmeyer.jisp.environment.StandardEnvironment;
import org.junit.jupiter.api.Test;

class StandardEnvironmentTest {

    @Test
    public void canSetAndGetEnvironmentVariables() {
        StandardEnvironment env = new StandardEnvironment();

        env.setVariable("x", new NumberLiteral(120.4));

        assertThat(env.lookUpVariable("x")).isEqualTo(new NumberLiteral(120.4));
    }

    @Test
    public void returnsNullForUndefinedVariables() {
        StandardEnvironment env = new StandardEnvironment();

        assertThat(env.lookUpVariable("x")).isNull();
    }

    @Test
    public void canExtendEnvironmentAndLookUpInParent() {
        StandardEnvironment env = new StandardEnvironment();
        env.setVariable("x", new NumberLiteral(120.4));

        Environment extended = env.extendEnvironment(new HashMap<>() {{
            put("y", new NumberLiteral(123.0));
        }});

        assertThat(extended.lookUpVariable("x")).isEqualTo(new NumberLiteral(120.4));
        assertThat(extended.lookUpVariable("y")).isEqualTo(new NumberLiteral(123.));
    }

}