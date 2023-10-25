package utility;

import java.util.*;

/**
 * This class extends ArrayList, modifying it by placing a limit on capacity.
 */
public class LimitedArrayList<E> extends ArrayList<E> {

    // FIELD
    /**
     * Maximum allowed capacity of this list.
     */
    private final int maxCapacity;


    // CONSTRUCTOR
    /**
     * Constructs an empty list with the specified maximum capacity.
     *
     * @param maxCapacity the maximum capacity of the list
     * @throws IllegalArgumentException if the specified maximum capacity is zero or negative
     */
    public LimitedArrayList(int maxCapacity) {
        super(maxCapacity);
        if (maxCapacity > 0) {
            this.maxCapacity = maxCapacity;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + maxCapacity);
        }
    }


    // METHODS
    /**
     * Returns the maximum number of elements allowed in this list.
     *
     * @return the maximum number of elements allowed in this list
     */
    public int maxCapacity() {

        return maxCapacity;
    }


    /**
     * @throws UnsupportedOperationException always thrown; this method is not supported by this list implementation
     */
    @Override
    public void trimToSize() {
        throw new UnsupportedOperationException();
    }


    /**
     * @throws UnsupportedOperationException always thrown; this method is not supported by this list implementation
     */
    @Override
    public void ensureCapacity(int minCapacity) {
        throw new UnsupportedOperationException();
    }


    /**
     * @throws IllegalStateException if the size of this list attempts to exceed the maximum capacity
     */
    @Override
    public boolean add(E e) {

        if (super.size() < maxCapacity) {

            return super.add(e);
        } else {

            throw new IllegalStateException("List attempted to exceed its maximum allowed capacity");
        }
    }


    /**
     * @throws IllegalStateException if the size of this list attempts to exceed the maximum capacity
     */
    @Override
    public void add(int index, E element) {

        if (super.size() < maxCapacity) {

            super.add(index, element);
        } else {

            throw new IllegalStateException("List attempted to exceed its maximum allowed size");
        }
    }


    /**
     * @throws IllegalStateException if the size of this list attempts to exceed the maximum capacity
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {

        if ((maxCapacity - super.size()) >= c.size()) {

            return super.addAll(c);
        } else {

            throw new IllegalStateException("List attempted to exceed its maximum allowed size");
        }
    }


    /**
     * @throws IllegalStateException if the size of this list attempts to exceed the maximum capacity
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {

        if ((maxCapacity - super.size()) >= c.size()) {

            return super.addAll(index, c);
        } else {

            throw new IllegalStateException("List attempted to exceed its maximum allowed size");
        }
    }
}
