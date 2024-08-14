package net.fredrikmeyer.jisp;

public interface Environment {
    LispValue lookUpVariable(String name);
    void setVariable(String name, LispValue value);
}
