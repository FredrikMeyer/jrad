package net.fredrikmeyer.jisp.repl;

public sealed interface ReplResult {

    final class Quit implements ReplResult {

    }

    record StringValue(String value) implements ReplResult {

    }
}
