package net.fredrikmeyer.jisp;

import java.io.IOException;
import net.fredrikmeyer.jisp.repl.Repl;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.TerminalBuilder;

public class Main {

    public static void main(String[] args) throws IOException {
        var repl = new Repl();
        var terminal = TerminalBuilder.terminal();
        LineReader lineReader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(new StringsCompleter("_env", "exit"))
            .appName("jisp")
            .build();
        repl.run(lineReader);
    }
}