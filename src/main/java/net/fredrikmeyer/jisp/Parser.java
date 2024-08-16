package net.fredrikmeyer.jisp;

import java.util.List;
import net.fredrikmeyer.jisp.tokenizer.Token;

public interface Parser {
    LispExpression parse(List<Token> tokens);
}
