package net.fredrikmeyer.jisp;

import java.util.List;
import net.fredrikmeyer.jisp.LispExpression.LispSymbol;
import net.fredrikmeyer.jisp.LispExpression.Procedure.BuiltInProcedure;
import net.fredrikmeyer.jisp.environment.Environment;

public sealed interface LispExpression permits
    LispExpression.Procedure,
    LispList,
    LispLiteral,
    LispSymbol {

    sealed interface Procedure extends LispExpression permits BuiltInProcedure,
        Procedure.UserProcedure {

        non-sealed abstract class BuiltInProcedure implements Procedure {

            private final String name;

            public BuiltInProcedure(String name) {
                this.name = name;
            }

            public abstract LispExpression apply(LispExpression... values);

            @Override
            public String toString() {
                return "BuiltInProcedure{" + name + "}";
            }
        }

        /**
         * A UserProcedure is a procedure defined by the "user", for example by making a lambda
         * function. It captures the environment it was created in, together with a list of
         * arguments.
         *
         * @param environment
         * @param arguments
         * @param body
         */
        record UserProcedure(Environment environment, List<String> arguments,
                             LispExpression body) implements Procedure {

        }

    }

    record LispSymbol(String name) implements LispExpression {

        @Override
        public String toString() {
            return name;
        }
    }
}

