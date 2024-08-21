package net.fredrikmeyer.jisp;

public non-sealed abstract class BuiltInProcedure implements LispExpression.Procedure {
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
