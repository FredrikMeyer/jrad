package net.fredrikmeyer.jisp;

import java.util.Objects; non-sealed public class LispSymbol implements LispExpression {
    private final String name;

    public LispSymbol(String name) {
        this.name = name;
    }

    public String value() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LispSymbol that = (LispSymbol) o;
        return Objects.equals(name,
                that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
