package net.fredrikmeyer.jisp;

import java.util.List;

public interface Parser {
    LispExpression parse(List<Token> tokens);
}
