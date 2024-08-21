package net.fredrikmeyer.jisp;

import java.util.Scanner;
import net.fredrikmeyer.jisp.environment.Environment;
import net.fredrikmeyer.jisp.environment.StandardEnvironment;
import net.fredrikmeyer.jisp.parser.ParserImpl;
import net.fredrikmeyer.jisp.tokenizer.TokenizerImpl;

public class REPLImpl implements REPL {

    private final Scanner scanner = new Scanner(System.in);

    private final Environment env = new StandardEnvironment();
    private final IEvalApply evalApply = new EvalApplyImpl();

    @Override
    public String readInput() {
        System.out.print("> ");
        return scanner.nextLine();
    }

    @Override
    public LispExpression evaluate(String input) {
        LispExpression parse = new ParserImpl().parse(new TokenizerImpl().tokenize(input));

        return evalApply.eval(parse, env);
    }

    @Override
    public void printOutput(Object output) {
        System.out.println(output);
    }

    @Override
    public void run() {
        while (true) {
            String input = readInput();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting REPL. Goodbye!");
                break;
            }
            try {
                LispExpression result = evaluate(input);
                printOutput(result);
            }
            catch (Exception e) {
                printOutput(e);
            }
        }
    }
}
