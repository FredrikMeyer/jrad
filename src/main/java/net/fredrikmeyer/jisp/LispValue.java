package net.fredrikmeyer.jisp;

import java.util.List;

public sealed interface LispValue permits NumberValue, Procedure, StringValue, SymbolValue {

}

record NumberValue(double d) implements LispValue {

}

record StringValue(String s) implements LispValue {

}

record SymbolValue(String name) implements LispValue {

}

record UserProcedure(Environment environment, List<String> arguments,
                     LispExpression body) implements Procedure {

}

non-sealed abstract class BuiltInProcedure implements Procedure {

    //private final List<String> arguments;

    BuiltInProcedure() {
    }

    abstract LispValue apply(LispValue... values);
}
