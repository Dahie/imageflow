package de.danielsenff.imageflow.utils;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * http://stackoverflow.com/questions/1098117/can-one-do-a-for-each-loop-in-java-in-reverse-order
 * @author danielsenff
 *
 * @param <T>
 */
public class Reversed<T> implements Iterable<T> {
    private final List<T> original;

    public Reversed(final List<T> original) {
        this.original = original;
    }

    public Iterator<T> iterator() {
        final ListIterator<T> i = original.listIterator(original.size());

        return new Iterator<T>() {
            public boolean hasNext() { return i.hasPrevious(); }
            public T next() { return i.previous(); }
            public void remove() { /*return i.remove(); */}
        };
    }

    public static <T> Reversed<T> reversed(List<T> original) {
        return new Reversed<T>(original);
    }
}