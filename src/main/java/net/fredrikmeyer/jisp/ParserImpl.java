package net.fredrikmeyer.jisp;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class ParserImpl implements Parser {
    @Override
    public LispExpression parse(List<Token> tokens) {
//        Stack<Token> stack = new Stack<>();
//
//        while (!stack.isEmpty()) {
//            Token token = stack.pop();
//
//        }

        Token firstToken = tokens.getFirst();
        Objects.requireNonNull(firstToken);

        LispExpression exp = switch (firstToken) {
            case Token.NumberLiteral n -> throw new IllegalStateException("not yet");
            case Token.StringLiteral s -> new LispLiteral.StringLiteral(s.value());
            case Token.Symbol s ->throw new IllegalStateException("not yet");
            case Token.EOF e -> throw new IllegalStateException("not yet");
            case Token.LeftParen l -> throw new IllegalStateException("not yet");
            case Token.RightParen _ -> throw new IllegalStateException("not yet");
        };

        return exp;
    }
}
