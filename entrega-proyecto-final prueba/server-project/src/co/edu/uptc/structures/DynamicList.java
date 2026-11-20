package co.edu.uptc.structures;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class DynamicList<T> implements Iterable<T>, Serializable {

    private static final long serialVersionUID = 1L;
    private transient Node<T> head;
    private transient Node<T> tail;
    private transient int size;

    public void add(T element) {
        Node<T> node = new Node<>(element);
        if (head == null) {
            head = node;
        } else {
            tail.setNext(node);
        }
        tail = node;
        size++;
    }

    public T get(int index) {
        return nodeAt(index).getData();
    }

    public T remove(int index) {
        checkIndex(index);
        Node<T> previous = index == 0 ? null : nodeAt(index - 1);
        Node<T> target = previous == null ? head : previous.getNext();
        unlink(previous, target);
        return target.getData();
    }

    public boolean remove(T element) {
        Node<T> previous = null;
        Node<T> current = head;
        while (current != null) {
            if (areEqual(current.getData(), element)) {
                unlink(previous, current);
                return true;
            }
            previous = current;
            current = current.getNext();
        }
        return false;
    }

    public void removeIf(Predicate<T> condition) {
        Node<T> previous = null;
        Node<T> current = head;
        while (current != null) {
            Node<T> next = current.getNext();
            if (condition.test(current.getData())) {
                unlink(previous, current);
            } else {
                previous = current;
            }
            current = next;
        }
    }

    public boolean contains(T element) {
        for (T item : this) {
            if (areEqual(item, element)) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    public DynamicList<T> copy() {
        DynamicList<T> result = new DynamicList<>();
        for (T item : this) {
            result.add(item);
        }
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new ListIterator();
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private Node<T> nodeAt(int index) {
        checkIndex(index);
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        return current;
    }

    private void unlink(Node<T> previous, Node<T> node) {
        if (previous == null) {
            head = node.getNext();
        } else {
            previous.setNext(node.getNext());
        }
        if (node == tail) {
            tail = previous;
        }
        node.setNext(null);
        size--;
    }

    private boolean areEqual(T a, T b) {
        return a == null ? b == null : a.equals(b);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(size);
        for (T item : this) {
            out.writeObject(item);
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            add((T) in.readObject());
        }
    }

    private class ListIterator implements Iterator<T> {

        private Node<T> previous;     
        private Node<T> lastReturned; 
        private Node<T> next = head;  

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public T next() {
            if (next == null) {
                throw new NoSuchElementException();
            }
            if (lastReturned != null) {
                previous = lastReturned;
            }
            lastReturned = next;
            next = next.getNext();
            return lastReturned.getData();
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            Node<T> following = lastReturned.getNext();
            unlink(previous, lastReturned);
            lastReturned = null;
            next = following;
        }
    }
}