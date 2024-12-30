package net.fredrikmeyer.jisp.repl;

import java.io.IOException;
import net.fredrikmeyer.jisp.EvalApplyImpl;
import net.fredrikmeyer.jisp.IEvalApply;
import net.fredrikmeyer.jisp.LispExpression;
import net.fredrikmeyer.jisp.environment.Environment;
import net.fredrikmeyer.jisp.environment.StandardEnvironment;
import net.fredrikmeyer.jisp.parser.ParserImpl;
import net.fredrikmeyer.jisp.tokenizer.TokenizerImpl;
import org.intellij.lang.annotations.Language;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;

public class Repl {

    public void run(LineReader reader) {
        Environment env = new StandardEnvironment();
        IEvalApply evalApply = new EvalApplyImpl();

        while (true) {
            String line;
            try {
                line = reader.readLine("> ");
            } catch (UserInterruptException e) {
                System.out.println("Exiting...");
                break;
            }
            if (line == null || line.strip().equalsIgnoreCase("exit")) {
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

    private LispExpression evaluateLastLine(@Language("scheme") String line,
        IEvalApply evalApply,
        Environment env) {
        LispExpression parsed = parse(line);
        return evalApply.eval(parsed, env);
    }

    private LispExpression parse(@Language("scheme") String input) {
        return new ParserImpl().parse(new TokenizerImpl().tokenize(input));
    }
}
