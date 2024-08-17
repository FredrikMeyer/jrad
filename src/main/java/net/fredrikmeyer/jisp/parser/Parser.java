package net.fredrikmeyer.jisp.parser;

import java.util.List;
import net.fredrikmeyer.jisp.LispExpression;
import net.fredrikmeyer.jisp.tokenizer.Token;

public interface Parser {
    LispExpression parse(List<Token> tokens);
}
