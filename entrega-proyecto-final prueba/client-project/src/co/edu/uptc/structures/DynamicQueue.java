package co.edu.uptc.structures;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class DynamicQueue<T> implements Iterable<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private final DynamicList<T> elements = new DynamicList<>();

    public void enqueue(T element) {
        elements.add(element);
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("QUEUE_EMPTY");
        }
        return elements.remove(0);
    }

    public T peek() {
        return isEmpty() ? null : elements.get(0);
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public int size() {
        return elements.size();
    }

    public T removeFirstMatch(Predicate<T> condition) {
        Iterator<T> iterator = elements.iterator();
        while (iterator.hasNext()) {
            T candidate = iterator.next();
            if (condition.test(candidate)) {
                iterator.remove();
                return candidate;
            }
        }
        return null;
    }

    public void removeIf(Predicate<T> condition) {
        elements.removeIf(condition);
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }
}