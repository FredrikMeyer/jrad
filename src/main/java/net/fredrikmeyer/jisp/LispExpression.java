package net.fredrikmeyer.jisp;

import java.util.List;
import net.fredrikmeyer.jisp.LispExpression.LispSymbol;
import net.fredrikmeyer.jisp.LispExpression.Nil;
import net.fredrikmeyer.jisp.LispExpression.Ok;
import net.fredrikmeyer.jisp.LispExpression.Procedure;
import net.fredrikmeyer.jisp.LispExpression.Procedure.BuiltInProcedure;
import net.fredrikmeyer.jisp.environment.Environment;

public sealed interface LispExpression permits LispSymbol, Nil, Ok, Procedure, LispList,
    LispLiteral {

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

            @Override
            public String toString() {
                return "UserProcedure{" +
                       "arguments=" + arguments +
                       ", body=" + body +
                       '}';
            }
        }

    }

    record LispSymbol(String name) implements LispExpression {

        @Override
        public String toString() {
            return name;
        }
    }

    record Nil() implements LispExpression {
        @Override
        public String toString() {
            return "nul";
        }
    }

    record Ok() implements LispExpression {
    }
}

