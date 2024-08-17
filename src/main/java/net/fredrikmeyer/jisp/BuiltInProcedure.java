package net.fredrikmeyer.jisp;

import net.fredrikmeyer.jisp.LispValue.Procedure;

public non-sealed abstract class BuiltInProcedure implements Procedure {

    public BuiltInProcedure() {
    }

    public abstract LispValue apply(LispValue... values);
}
