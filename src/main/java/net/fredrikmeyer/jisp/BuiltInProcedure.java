package net.fredrikmeyer.jisp;

public non-sealed abstract class BuiltInProcedure implements LispExpression.Procedure {

    public BuiltInProcedure() {
    }

    public abstract LispExpression apply(LispExpression... values);
}
