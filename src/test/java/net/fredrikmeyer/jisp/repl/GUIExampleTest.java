package net.fredrikmeyer.jisp.repl;

import static org.assertj.core.api.Assertions.assertThat;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.junit.jupiter.api.Test;

class GUIExampleTest {

    // Test implementation of GUIExample that doesn't create actual GUI components
    static class TestableGUIExample extends GUIExample {
        private StringBuilder outputText = new StringBuilder();
        private String environmentText = "";

        public TestableGUIExample() {
            // Don't initialize GUI components
            repl = new Repl();
            outputArea = new JTextArea();
            environmentArea = new JTextArea();
        }

        @Override
        protected void processInputText(String input) {
            // Capture output to our StringBuilder instead of JTextArea
            outputText.append(">> ").append(input).append("\n");

            ReplResult result = repl.write(input);

            if (result instanceof ReplResult.Quit) {
                outputText.append("Quitting...\n");
                // Don't actually exit
            } else if (result instanceof ReplResult.StringValue stringValue) {
                outputText.append(stringValue.value()).append("\n\n");
            }

            updateEnvironmentDisplay();
        }

        @Override
        protected void updateEnvironmentDisplay() {
            ReplResult envResult = repl.write("_env");
            if (envResult instanceof ReplResult.StringValue stringValue) {
                environmentText = stringValue.value();
                // Update the JTextArea for test verification
                environmentArea.setText(environmentText);
            }
        }

        public String getOutputText() {
            return outputText.toString();
        }

        public String getEnvironmentText() {
            return environmentText;
        }
    }

    @Test
    void testProcessInput() {
        TestableGUIExample gui = new TestableGUIExample();

        // Test simple expression evaluation
        gui.processInputText("(+ 1 2)");
        assertThat(gui.getOutputText()).contains(">> (+ 1 2)");
        assertThat(gui.getOutputText()).contains("3.0");

        // Test environment display
        assertThat(gui.getEnvironmentText()).contains("ENV:");
        assertThat(gui.getEnvironmentText()).contains("StandardEnvironment");
    }

    @Test
    void testDefineVariable() {
        TestableGUIExample gui = new TestableGUIExample();

        // Define a variable
        gui.processInputText("(define x 10)");

        // Verify the variable is defined in the environment
        assertThat(gui.getEnvironmentText()).contains("x");

        // Use the variable in an expression
        gui.processInputText("(+ x 5)");
        assertThat(gui.getOutputText()).contains("15.0");
    }

    @Test
    void testEnvironmentCommand() {
        TestableGUIExample gui = new TestableGUIExample();

        // Test the _env command
        gui.processInputText("_env");
        assertThat(gui.getOutputText()).contains("ENV:");
        assertThat(gui.getOutputText()).contains("StandardEnvironment");
    }

    @Test
    void testComplexExpression() {
        TestableGUIExample gui = new TestableGUIExample();

        // Define a simple function using lambda
        gui.processInputText("(define square (lambda (x) (* x x)))");

        // Use the function
        gui.processInputText("(square 5)");
        assertThat(gui.getOutputText()).contains("25.0");

        // Verify the function is in the environment
        assertThat(gui.getEnvironmentText()).contains("square");
    }

    @Test
    void testMultipleExpressions() {
        TestableGUIExample gui = new TestableGUIExample();

        // Define variables
        gui.processInputText("(define x 10)");
        gui.processInputText("(define y 20)");

        // Perform calculations
        gui.processInputText("(+ x y)");
        assertThat(gui.getOutputText()).contains("30.0");

        gui.processInputText("(* x y)");
        assertThat(gui.getOutputText()).contains("200.0");

        // Verify both variables are in the environment
        assertThat(gui.getEnvironmentText()).contains("x");
        assertThat(gui.getEnvironmentText()).contains("y");
    }
}
