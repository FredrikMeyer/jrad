# ráð Project Guidelines

## Project Overview
ráð (Java + gammel-norsk for "advice/råd") is a simple Lisp interpreter written in Java. The project implements a basic Lisp language with a REPL (Read-Eval-Print Loop) interface.

## Tech Stack
- **Language**: Java 22
- **Build System**: Maven
- **Testing**: JUnit Jupiter, AssertJ, PITest (mutation testing)
- **Dependencies**: JLine (terminal interface), JetBrains Annotations

## Project Structure
The project follows a standard Maven directory structure:

```
src/
├── main/java/net/fredrikmeyer/jisp/
│   ├── environment/  # Handles the Lisp environment (variables, functions)
│   ├── evaluator/    # Evaluates Lisp expressions
│   ├── parser/       # Parses Lisp code into an AST
│   ├── repl/         # Read-Eval-Print Loop implementation
│   └── tokenizer/    # Tokenizes Lisp code
└── test/
    ├── java/         # Test classes
    └── resources/    # Test resources (Lisp code files)
```

## Building and Running

### Building the Project
```bash
# Using Maven
mvn package

# Using Just
just build
```

### Running the REPL
```bash
# Using Just
just repl

# Using Maven directly
mvn -DskipTests=true exec:java -Dexec.mainClass=net.fredrikmeyer.jisp.Main
```

## Testing

### Running Tests
```bash
# Using Just
just test

# Using Maven directly
mvn test
```

### Mutation Testing
```bash
# Run mutation tests
just pitest

# Run mutation tests and open coverage report
just mutation-coverage
```

## Development Workflow

### Main Components
1. **Tokenizer**: Breaks input into tokens
2. **Parser**: Parses tokens into Lisp expressions
3. **EvalApply**: Evaluates Lisp expressions
4. **Environment**: Stores variables and functions
5. **REPL**: Provides an interactive interface

### Best Practices
1. **Write Tests**: Add tests for new features and bug fixes
2. **Run Mutation Tests**: Ensure high-quality test coverage
3. **Follow Existing Patterns**: Maintain consistency with the current codebase
4. **Document Code**: Add comments for complex logic
5. **Update README**: Document new features or changes

## Lisp Features
The interpreter supports basic Lisp syntax including:
- Atoms (numbers, strings, booleans)
- Quotation
- Assignment (define, set!)
- Sequences (begin)
- Lambdas
- Conditionals (if)

See the README.md for more details on supported syntax and planned features.