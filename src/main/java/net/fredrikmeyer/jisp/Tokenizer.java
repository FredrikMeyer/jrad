package net.fredrikmeyer.jisp;

import java.util.List;

public interface Tokenizer {
    List<Token> tokenize(String input);
}
