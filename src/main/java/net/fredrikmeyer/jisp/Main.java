package net.fredrikmeyer.jisp;

import sun.misc.Signal;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");
        Signal.handle(new Signal("INT"),  // SIGINT
            _ -> System.exit(0));
        new REPLImpl().run();
    }
}