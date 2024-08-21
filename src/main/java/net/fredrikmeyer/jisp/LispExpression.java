package net.fredrikmeyer.jisp;

public sealed interface LispExpression permits LispLiteral, LispSymbol, LispList {
}

