package net.fredrikmeyer.jisp;

import java.util.Objects;
import net.fredrikmeyer.jisp.LispExpression.LispSymbol;

public sealed interface LispExpression permits LispExpression.Procedure, LispList, LispLiteral,
    LispSymbol {

    sealed interface Procedure extends LispExpression permits BuiltInProcedure, UserProcedure {

    }

    record LispSymbol(String name) implements LispExpression {

        @Override
        public String toString() {
            return name;
        }
    }


}

