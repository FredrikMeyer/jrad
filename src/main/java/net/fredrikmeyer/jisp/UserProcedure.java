package net.fredrikmeyer.jisp;

import java.util.List;
import net.fredrikmeyer.jisp.environment.Environment;

public record UserProcedure(Environment environment, List<String> arguments,
                            LispExpression body) implements Procedure {

}
