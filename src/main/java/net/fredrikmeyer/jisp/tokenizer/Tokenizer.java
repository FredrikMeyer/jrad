package net.fredrikmeyer.jisp.tokenizer;

import java.util.List;

public interface Tokenizer {
    List<Token> tokenize(String input);
}
