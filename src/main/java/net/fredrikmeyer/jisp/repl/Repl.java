package net.fredrikmeyer.jisp.repl;

import net.fredrikmeyer.jisp.evaluator.StandardEvalApply;
import net.fredrikmeyer.jisp.evaluator.EvalApply;
import net.fredrikmeyer.jisp.LispExpression;
import net.fredrikmeyer.jisp.environment.Environment;
import net.fredrikmeyer.jisp.environment.StandardEnvironment;
import net.fredrikmeyer.jisp.parser.ParserImpl;
import net.fredrikmeyer.jisp.repl.ReplResult.Quit;
import net.fredrikmeyer.jisp.repl.ReplResult.StringValue;
import net.fredrikmeyer.jisp.tokenizer.TokenizerImpl;
import org.intellij.lang.annotations.Language;

public class Repl {

    private final Environment environment;
    private final EvalApply evalApply;

    public Repl() {
        this.environment = new StandardEnvironment();
        this.evalApply = new StandardEvalApply();
    }

    public ReplResult write(@Language("scheme") String input) {
        if (input == null || input.strip().equalsIgnoreCase("exit")) {
            return new Quit();
        }
        if (input.equalsIgnoreCase("_env")) {
            return new StringValue("ENV: " + environment);
        }

        try {
            LispExpression result = this.evaluateLastLine(input);
            return new StringValue(result.toString());
        } catch (Exception e) {
            return new StringValue(e.getMessage());
        }
    }

    private LispExpression evaluateLastLine(@Language("scheme") String line) {
        LispExpression parsed = parse(line);
        return evalApply.eval(parsed, environment);
    }

    private LispExpression parse(@Language("scheme") String input) {
        return new ParserImpl().parse(new TokenizerImpl().tokenize(input));
    }
}
