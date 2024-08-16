package net.fredrikmeyer.jisp;

public non-sealed abstract class BuiltInProcedure implements Procedure {

    public BuiltInProcedure() {
    }

    public abstract LispValue apply(LispValue... values);
}
