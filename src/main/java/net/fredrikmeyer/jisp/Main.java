package net.fredrikmeyer.jisp;

import java.io.IOException;
import net.fredrikmeyer.jisp.repl.JLineRepl;

public class Main {
    public static void main(String[] args) throws IOException {
        var repl = new JLineRepl();

        repl.start();
    }
}