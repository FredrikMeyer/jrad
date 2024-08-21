package net.fredrikmeyer.jisp;

public interface REPL {
    /**
     * Reads a line of input from the user.
     *
     * @return the input string from the user
     */
    String readInput();

    /**
     * Evaluates the given input command.
     *
     * @param input the command to evaluate
     * @return the result of the evaluation
     */
    LispExpression evaluate(String input);

    /**
     * Prints the output to the user.
     *
     * @param output the object to print
     */
    void printOutput(Object output);

    /**
     * Runs the REPL loop until the exit condition is met.
     */
    void run();
}
