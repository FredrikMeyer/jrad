package net.fredrikmeyer.jisp.repl;

import java.io.IOException;
import net.fredrikmeyer.jisp.repl.ReplResult.Quit;
import net.fredrikmeyer.jisp.repl.ReplResult.StringValue;
import org.intellij.lang.annotations.Language;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.TerminalBuilder;

public class JLineRepl {

    private boolean shouldRun = true;

    public void start() throws IOException {
        var repl = new Repl();
        var terminal = TerminalBuilder.terminal();

        var reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(new StringsCompleter("_env", "exit"))
            .appName("jisp")
            .build();

        while (shouldRun) {
            @Language("scheme") var line = getLine(reader);
            var res = repl.write(line);

            switch (res) {
                case Quit _ -> {
                    System.out.println("Quiting.");
                    shouldRun = false;
                }
                case StringValue stringValue -> {
                    System.out.println(stringValue);
                }
            }
        }
    }

    private String getLine(LineReader reader) {
        try {
            return reader.readLine(">> ");
        } catch (UserInterruptException e) {
            this.shouldRun = false;
            return "exit"; // Hacky, for nå
        }
    }
}
