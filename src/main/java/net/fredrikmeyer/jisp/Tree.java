package net.fredrikmeyer.jisp;

import java.util.ArrayList;
import java.util.List;

public class Tree {
    private List<Tree> children = new ArrayList<>();
    private String value;

    public Tree(String value) {
        this.value = value;
    }

    public void addChild(Tree child) {
        children.add(child);
    }

    public Tree childAt(int index) {
        if (index < 0 || index >= children.size()) {
            throw new IndexOutOfBoundsException("Invalid index");
        }

        return children.get(index);
    }

    public String getValue() {
        return value;
    }

    static class Node {

    }
}
