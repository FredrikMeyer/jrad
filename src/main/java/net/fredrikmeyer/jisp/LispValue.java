package net.fredrikmeyer.jisp;

import net.fredrikmeyer.jisp.LispValue.BoolValue;
import net.fredrikmeyer.jisp.LispValue.NumberValue;
import net.fredrikmeyer.jisp.LispValue.Procedure;
import net.fredrikmeyer.jisp.LispValue.StringValue;
import net.fredrikmeyer.jisp.LispValue.SymbolValue;

public sealed interface LispValue permits NumberValue, Procedure, StringValue,
    BoolValue, SymbolValue {

    record BoolValue(Boolean value) implements LispValue {

    }

    record SymbolValue(String name) implements LispValue {

    }

    record StringValue(String s) implements LispValue {

    }

    record NumberValue(double d) implements LispValue {

    }

    sealed interface Procedure extends LispValue permits BuiltInProcedure, UserProcedure {

    }

}

