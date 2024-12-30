package net.fredrikmeyer.jisp;

import java.io.IOException;
import net.fredrikmeyer.jisp.environment.Environment;
import net.fredrikmeyer.jisp.environment.StandardEnvironment;
import net.fredrikmeyer.jisp.parser.ParserImpl;
import net.fredrikmeyer.jisp.tokenizer.TokenizerImpl;
import org.intellij.lang.annotations.Language;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Main {

    public static void main(String[] args) throws IOException {
        Terminal terminal = TerminalBuilder.terminal();

        LineReader reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(new StringsCompleter("describe", "create"))
            .appName("jisp")
            .build();

        Environment env = new StandardEnvironment();
        IEvalApply evalApply = new EvalApplyImpl();

        while (true) {
            String line = null;
            try {
                line = reader.readLine("> ");
            } catch (UserInterruptException e) {
                System.out.println("Exiting...");
                break;
            }
            if (line == null || line.equalsIgnoreCase("exit")) {
                break;
            }
            if (line.equalsIgnoreCase("_env")) {
                System.out.println(">> ENV: " + env);
                continue;
            }
            reader.getHistory().add(line);

            try {
                LispExpression result = evaluateLastLine(line, evalApply, env);
                System.out.println(">>: " + result);
            } catch (Exception e) {
                System.out.println(">>: " + e.getMessage());
            }
        }
    }

    private static LispExpression evaluateLastLine(@Language("scheme") String line,
        IEvalApply evalApply,
        Environment env) {
        LispExpression parsed = parse(line);
        return evalApply.eval(parsed, env);
    }

    private static LispExpression parse(@Language("scheme") String input) {
        return new ParserImpl().parse(new TokenizerImpl().tokenize(input));
    }
}