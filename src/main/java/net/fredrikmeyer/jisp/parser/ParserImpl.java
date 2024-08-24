package net.fredrikmeyer.jisp.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import net.fredrikmeyer.jisp.LispExpression;
import net.fredrikmeyer.jisp.LispExpression.LispSymbol;
import net.fredrikmeyer.jisp.LispList;
import net.fredrikmeyer.jisp.LispLiteral;
import net.fredrikmeyer.jisp.tokenizer.Token;
import net.fredrikmeyer.jisp.tokenizer.Token.EOF;
import net.fredrikmeyer.jisp.tokenizer.Token.LeftParen;
import net.fredrikmeyer.jisp.tokenizer.Token.NumberLiteral;
import net.fredrikmeyer.jisp.tokenizer.Token.Quote;
import net.fredrikmeyer.jisp.tokenizer.Token.RightParen;
import net.fredrikmeyer.jisp.tokenizer.Token.StringLiteral;
import net.fredrikmeyer.jisp.tokenizer.Token.Symbol;
import org.jetbrains.annotations.NotNull;

public class ParserImpl implements Parser {

    @NotNull
    @Override
    public LispExpression parse(List<Token> inputTokens) {
        var tokens = new ArrayList<>(inputTokens);
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Plz not null");
        }

        if (tokens.size() == 1 && tokens.getFirst().getClass() == EOF.class) {
            // TODO, egen exception
            throw new RuntimeException("No tokens parsed.");
        }

        if (tokens.size() == 2) {
            Token first = tokens.getFirst();
            return switch (first) {
                case NumberLiteral numberLiteral -> parseNumberLiteral(numberLiteral);
                case StringLiteral stringLiteral -> parseStringLiteral(stringLiteral);
                case Symbol symbol -> new LispSymbol(symbol.value());
                default -> {
                    throw new RuntimeException("No tokens parsed.");
                }
            };
        }

        Stack<LispList> stack = new Stack<>();
        LispList res = null;

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.getClass() == EOF.class) {
                break;
            }
            switch (t) {
                case LeftParen _ -> {
                    stack.push(new LispList(new ArrayList<>()));
                }
                case RightParen _ -> {
                    if (stack.isEmpty()) {
                        throw new RuntimeException("Mismatched parentheses. Tokens: " + Arrays.toString(tokens.toArray()));
                    }
                    var popped = stack.pop();
                    if (stack.isEmpty()) {
                        res = popped;
                    } else {
                        stack.peek().append(popped); // This is a sublist, append to the parent list
                    }
                }
                case NumberLiteral numberLiteral -> {
                    if (!stack.isEmpty()) {
                        stack.peek().append(parseNumberLiteral(numberLiteral));
                    } else {
                        throw new RuntimeException(
                            "Should not get here. Illegal expression at position: "
                            + numberLiteral.position());
                    }
                }
                case StringLiteral stringLiteral -> {
                    if (!stack.isEmpty()) {
                        stack.peek().append(parseStringLiteral(stringLiteral));
                    } else {
                        throw new RuntimeException("Should not get here.");
                    }
                }
                case Symbol symbol -> {
                    if (!stack.isEmpty()) {
                        stack.peek().append(new LispSymbol(symbol.value()));
                    } else {
                        throw new RuntimeException("Should not get here.");
                    }
                }
                case Quote _ -> {
                    if (!stack.isEmpty()) {
                        stack.peek().append(new LispSymbol("quote"));
                    } else {
                        ArrayList<LispExpression> emptyList = new ArrayList<>();
                        emptyList.add(new LispSymbol("quote"));
                        LispList quote = new LispList(emptyList);
                        stack.push(quote);
                        tokens.add(tokens.size() - 2, new RightParen());
//                        tokens.add(new RightParen());
//                        throw new RuntimeException("Should not get here. Current token: " + t);
                    }
                }
                case EOF _ -> {
                    throw new IllegalArgumentException("Should not get here.");
                }
            }
        }

        assert stack.isEmpty();

        return Objects.requireNonNull(res);
    }

    private static LispLiteral.StringLiteral parseStringLiteral(Token.StringLiteral s) {
        return new LispLiteral.StringLiteral(s.value());
    }

    private static LispLiteral.NumberLiteral parseNumberLiteral(Token.NumberLiteral n) {
        return new LispLiteral.NumberLiteral(n.value());
    }
}
