package net.fredrikmeyer.jisp;
import java.io.IOException;
import net.fredrikmeyer.jisp.environment.Environment;
import net.fredrikmeyer.jisp.environment.StandardEnvironment;
import net.fredrikmeyer.jisp.parser.ParserImpl;
import net.fredrikmeyer.jisp.tokenizer.TokenizerImpl;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Main {

    public static void main(String[] args) throws IOException {
        //Signal.handle(new Signal("INT"),  // SIGINT
//            _ -> System.exit(0));
        //new REPLImpl().run();

        Terminal terminal = TerminalBuilder.terminal();
        LineReader reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(new StringsCompleter("describe", "create"))
            .build();

        Environment env = new StandardEnvironment();
        IEvalApply evalApply = new EvalApplyImpl();

        while (true) {
            String line = reader.readLine("> ");
            if (line == null || line.equalsIgnoreCase("exit")) {
                break;
            }
            reader.getHistory().add(line);

            LispExpression parsed = parse(line);

            LispExpression result = evalApply.eval(parsed, env);

            System.out.println(">>: " + result);
        }
    }

    private static LispExpression parse(String input) {
        return new ParserImpl().parse(new TokenizerImpl().tokenize(input));
    }
}