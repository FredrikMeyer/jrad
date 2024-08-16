package net.fredrikmeyer.jisp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import net.fredrikmeyer.jisp.tokenizer.Token;
import net.fredrikmeyer.jisp.tokenizer.Token.EOF;
import net.fredrikmeyer.jisp.tokenizer.Token.NumberLiteral;
import net.fredrikmeyer.jisp.tokenizer.Token.StringLiteral;
import net.fredrikmeyer.jisp.tokenizer.Token.Symbol;
import org.jetbrains.annotations.NotNull;

public class ParserImpl implements Parser {

    @NotNull
    @Override
    public LispExpression parse(List<Token> tokens) {
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

        for (Token t : tokens) {
            if (t.getClass() == Token.EOF.class) {
                break;
            }
            switch (t) {
                case Token.LeftParen _ -> {
                    stack.push(new LispList(new ArrayList<>()));
                }
                case Token.RightParen _ -> {
                    if (stack.isEmpty()) {
                        throw new RuntimeException("Mismatched parentheses.");
                    }
                    var popped = stack.pop();
                    if (stack.isEmpty()) {
                        stack.push(popped); // This is the top-level list
                    } else {
                        stack.peek().append(popped); // This is a sublist, append to the parent list
                    }
                }
                case Token.NumberLiteral numberLiteral -> {
                    if (!stack.isEmpty()) {
                        stack.peek().append(parseNumberLiteral(numberLiteral));
                    } else {
                        //exp = parseNumberLiteral(numberLiteral);
                        throw new RuntimeException("Should not get here.");
                    }
                }
                case Token.StringLiteral stringLiteral -> {
                    if (!stack.isEmpty()) {
                        stack.peek().append(parseStringLiteral(stringLiteral));
                    } else {
                        //exp = parseStringLiteral(stringLiteral);
                        throw new RuntimeException("Should not get here.");
                    }
                }
                case Token.Symbol symbol -> {
                    if (!stack.isEmpty()) {
                        stack.peek().append(new LispSymbol(symbol.value()));
                    } else {
                        //exp = new LispSymbol(symbol.value());
                        throw new RuntimeException("Should not get here.");
                    }
                }
                case EOF _ -> {
                    throw new IllegalArgumentException("shoudl not hete");
                }
            }
        }

        assert stack.size() == 1;

        return Objects.requireNonNull(stack.pop());
    }

    private static LispLiteral.StringLiteral parseStringLiteral(Token.StringLiteral s) {
        return new LispLiteral.StringLiteral(s.value());
    }

    private static LispLiteral.NumberLiteral parseNumberLiteral(Token.NumberLiteral n) {
        return new LispLiteral.NumberLiteral(n.value());
    }
}
