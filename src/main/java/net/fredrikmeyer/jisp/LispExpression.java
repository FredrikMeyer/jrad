package net.fredrikmeyer.jisp;

public sealed interface LispExpression permits LispExpression.Procedure, LispList, LispLiteral,
    LispSymbol {

    sealed interface Procedure extends LispExpression permits BuiltInProcedure, UserProcedure {

    }

}

