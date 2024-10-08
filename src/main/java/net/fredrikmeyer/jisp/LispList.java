package net.fredrikmeyer.jisp;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

non-sealed public class LispList implements LispExpression {

    private final List<LispExpression> elements;

    public LispList(List<LispExpression> elements) {
        this.elements = elements;
    }

    LispList(LispExpression... elements) {
        this.elements = List.of(elements);
    }

    public List<LispExpression> elements() {
        return elements;
    }

    public LispExpression car() {
        return elements.getFirst();
    }

    public LispExpression cadr() {
        return elements.get(1);
    }

    public LispExpression caddr() {
        return elements.get(2);
    }

    public LispExpression cadddr() {
        return elements.get(3);
    }

    public LispList cdr() {
        return new LispList(elements.subList(1, elements.size()));
    }

    public void append(LispExpression element) {
        elements.add(element);
    }

    public int length() {
        return elements.size();
    }

    @Override
    public String toString() {
        String collect = elements.stream()
            .map(LispExpression::toString)
            .collect(Collectors.joining(" "));
        return "(" + collect + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LispList lispList = (LispList) o;
        return Objects.equals(elements,
            lispList.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(elements);
    }
}
