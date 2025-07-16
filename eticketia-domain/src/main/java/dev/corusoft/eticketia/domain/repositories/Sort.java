package dev.corusoft.eticketia.domain.repositories;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Sort option for queries. You have to provide properties to sort by and a direction.
 **/
@Getter
@AllArgsConstructor
public class Sort {

    /**
     * The property to sort by. Must not be {@literal null} or empty.
     */
    private final String property;
    /**
     * The direction to sort by. Can be {@literal null}, which will be defaulted by the persistence store.
     */
    private final Direction direction;

    /**
     * Creates a new {@link Sort} instance.
     *
     * @param property  the property to sort by; must not be {@literal null} or empty.
     * @param direction the direction to sort by.
     * @return a new {@link Sort} instance.
     */
    public static Sort by(String property, Direction direction) {
        return new Sort(property, direction);
    }

    /**
     * Enumeration for sort directions.
     */
    public enum Direction {
        /**
         * Ascending order.
         */
        ASC,
        /**
         * Descending order.
         */
        DESC
    }
}