package net.fredrikmeyer.jisp;

public sealed interface LispValue permits NumberValue, Procedure, StringValue, SymbolValue {

}

