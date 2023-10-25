package utility;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This class extends LinkedHashMap, modifying it by placing a limit on capacity.
 */
public class LimitedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    // FIELD
    /**
     * Maximum allowed capacity of this map.
     */
    private final int maxCapacity;


    // CONSTRUCTOR
    /**
     * Constructs a LimitedLinkedHashMap instance with the specified maximum capacity.
     *
     * @param maxCapacity the maximum capacity
     */
    public LimitedLinkedHashMap(int maxCapacity) {
        super(maxCapacity);
        this.maxCapacity = maxCapacity;
    }


    // METHODS
    /**
     * Return the maximum number of key-value mappings allowed in this map.
     *
     * @return the maximum number of key-value mappings allowed in this map
     */
    public int maxCapacity() {

        return maxCapacity;
    }


    /**
     * @throws IllegalStateException if the size of this map attempts to exceed the maximum capacity
     */
    @Override
    public V put(K key, V value) {

        if (super.size() < maxCapacity) {

            return super.put(key, value);
        } else {

            throw new IllegalStateException("Map attempted to exceed its maximum allowed capacity");
        }
    }


    /**
     * @throws IllegalStateException if the size of this map attempts to exceed the maximum capacity
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

        int projectedSize = 0;

        for (K key : m.keySet()) {

            if (!this.containsKey(key)) {

                projectedSize++;
            }
        }

        projectedSize += this.keySet().size();

        if (projectedSize <= maxCapacity) {

            super.putAll(m);
        } else {

            throw new IllegalStateException("Map attempted to exceed its maximum allowed capacity");
        }
    }


    /**
     * @throws IllegalStateException if the size of this map attempts to exceed the maximum capacity
     */
    @Override
    public V putIfAbsent(K key, V value) {

        if ((this.size() < maxCapacity) || (this.containsKey(key))) {

             return super.putIfAbsent(key, value);
        } else {

            throw new IllegalStateException("Map attempted to exceed its maximum allowed capacity");
        }
    }



    /**
     * @throws IllegalStateException if the size of this map attempts to exceed the maximum capacity
     */
    @Override
    public V computeIfAbsent(K key,
                             Function<? super K, ? extends V> mappingFunction) {

        if ((this.size() < maxCapacity) || (this.containsKey(key)) || (mappingFunction.apply(key) == null)) {

            return super.computeIfAbsent(key, mappingFunction);
        } else {

            throw new IllegalStateException("Map attempted to exceed its maximum allowed capacity");
        }
    }


    /**
     * @throws IllegalStateException if the size of this map attempts to exceed the maximum capacity
     */
    @Override
    public V merge(K key, V value,
                   BiFunction<? super V, ? super V, ? extends V> remappingFunction) {

        if ((this.size() < maxCapacity) || (this.containsKey(key))) {

            return super.merge(key, value, remappingFunction);
        } else {

            throw new IllegalStateException("Map attempted to exceed its maximum allowed capacity");
        }
    }
}
