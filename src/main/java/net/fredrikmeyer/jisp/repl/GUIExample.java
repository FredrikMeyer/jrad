package net.fredrikmeyer.jisp.repl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.fredrikmeyer.jisp.repl.ReplResult.Quit;
import net.fredrikmeyer.jisp.repl.ReplResult.StringValue;

public class GUIExample {
    private JFrame frame;
    private JTextField inputField;
    protected JTextArea outputArea;
    protected JTextArea environmentArea;
    protected Repl repl;

    public GUIExample() {
        // Initialize the Repl
        repl = new Repl();

        // Create the main frame
        frame = new JFrame("Lisp Interpreter GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create the input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JButton submitButton = new JButton("Evaluate");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);

        // Create the output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setPreferredSize(new Dimension(400, 300));

        // Create the environment area
        environmentArea = new JTextArea();
        environmentArea.setEditable(false);
        environmentArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane environmentScrollPane = new JScrollPane(environmentArea);
        environmentScrollPane.setPreferredSize(new Dimension(400, 200));

        // Create a panel for the output and environment areas
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(outputScrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(new JLabel("Environment:"), BorderLayout.NORTH);
        southPanel.add(environmentScrollPane, BorderLayout.CENTER);

        // Add components to the frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(southPanel, BorderLayout.SOUTH);

        // Add action listeners
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processInput();
            }
        });

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processInput();
            }
        });

        // Set the size and make the frame visible
        frame.setSize(800, 600);
        frame.setVisible(true);

        // Initial welcome message
        outputArea.append("Welcome to the Lisp Interpreter!\n");
        outputArea.append("Type expressions to evaluate them, or '_env' to see the environment.\n\n");

        // Show initial environment state
        updateEnvironmentDisplay();
    }

    protected void processInput() {
        String input = inputField.getText();
        if (input.isEmpty()) {
            return;
        }

        processInputText(input);

        // Clear the input field
        inputField.setText("");
    }

    protected void processInputText(String input) {
        outputArea.append(">> " + input + "\n");

        ReplResult result = repl.write(input);

        if (result instanceof Quit) {
            outputArea.append("Quitting...\n");
            System.exit(0);
        } else if (result instanceof StringValue stringValue) {
            outputArea.append(stringValue.value() + "\n\n");
        }

        // Update the environment display
        updateEnvironmentDisplay();
    }

    protected void updateEnvironmentDisplay() {
        // Get the environment state by using the _env command
        ReplResult envResult = repl.write("_env");
        if (envResult instanceof StringValue stringValue) {
            String envText = stringValue.value();
            String prettyEnv = prettyFormatEnvironment(envText);
            environmentArea.setText(prettyEnv);
        }
    }

    /**
     * Formats the environment string in a more readable way.
     * 
     * @param envText The raw environment text from the REPL
     * @return A formatted string with better readability
     */
    protected String prettyFormatEnvironment(String envText) {
        if (envText == null || envText.isEmpty()) {
            return "";
        }

        // Extract the environment part (after "ENV: ")
        String envPart = envText.startsWith("ENV: ") ? envText.substring(5) : envText;

        // Basic formatting for now - we'll improve it
        StringBuilder formatted = new StringBuilder();
        formatted.append("Environment:\n\n");

        // Parse the environment string to extract variables and values
        // The format is typically: StandardEnvironment{env={var1=value1, var2=value2, ...}, parent=...}
        int envStart = envPart.indexOf("env=");
        int envEnd = envPart.indexOf(", parent=");

        if (envStart >= 0 && envEnd >= 0) {
            // Extract the env map part
            String envMap = envPart.substring(envStart + 5, envEnd - 1); // +5 to skip "env={"

            // Split by commas, but be careful about nested structures
            int depth = 0;
            StringBuilder currentVar = new StringBuilder();

            for (int i = 0; i < envMap.length(); i++) {
                char c = envMap.charAt(i);

                if (c == '{' || c == '[' || c == '(') {
                    depth++;
                    currentVar.append(c);
                } else if (c == '}' || c == ']' || c == ')') {
                    depth--;
                    currentVar.append(c);
                } else if (c == ',' && depth == 0) {
                    // End of a variable definition
                    String varDef = currentVar.toString().trim();
                    if (!varDef.isEmpty()) {
                        formatVariableDefinition(formatted, varDef);
                    }
                    currentVar = new StringBuilder();
                } else {
                    currentVar.append(c);
                }
            }

            // Don't forget the last variable
            String varDef = currentVar.toString().trim();
            if (!varDef.isEmpty()) {
                formatVariableDefinition(formatted, varDef);
            }

            // Add parent environment if present
            if (envEnd + 9 < envPart.length()) { // +9 for ", parent="
                String parentPart = envPart.substring(envEnd + 9, envPart.length() - 1);
                if (!parentPart.equals("null")) {
                    formatted.append("\nParent Environment:\n");
                    formatted.append("  ").append(parentPart.replace("\n", "\n  "));
                }
            }
        } else {
            // Fallback if we can't parse properly
            formatted.append(envPart);
        }

        return formatted.toString();
    }

    /**
     * Formats a single variable definition for display.
     * 
     * @param builder The StringBuilder to append to
     * @param varDef The variable definition string (e.g., "x=10.0")
     */
    private void formatVariableDefinition(StringBuilder builder, String varDef) {
        int equalsPos = varDef.indexOf('=');
        if (equalsPos > 0) {
            String name = varDef.substring(0, equalsPos).trim();
            String value = varDef.substring(equalsPos + 1).trim();

            builder.append("  ").append(name).append(" = ").append(value).append("\n");
        } else {
            builder.append("  ").append(varDef).append("\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUIExample();
            }
        });
    }
}
