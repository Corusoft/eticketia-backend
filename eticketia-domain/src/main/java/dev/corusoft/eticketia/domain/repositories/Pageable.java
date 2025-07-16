package dev.corusoft.eticketia.domain.repositories;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Abstract interface for pagination information.
 **/
@AllArgsConstructor
@Getter
public class Pageable {

    /**
     * The page to be returned, zero-based. Must not be negative.
     */
    private final int pageNumber;
    /**
     * The number of items to be returned. Must be greater than zero.
     */
    private final int pageSize;
    /**
     * The sorting parameters. Can be {@literal null}.
     */
    private final Sort sort;

    /**
     * Creates a new unsorted {@link Pageable}.
     *
     * @param pageNumber the page number, zero-based.
     * @param pageSize the page size.
     * @return a new {@link Pageable} instance.
     */
    public static Pageable of(int pageNumber, int pageSize) {
        return new Pageable(pageNumber, pageSize, null);
    }

    /**
     * Creates a new {@link Pageable} with sorting parameters.
     *
     * @param pageNumber the page number, zero-based.
     * @param pageSize the page size.
     * @param sort the sorting parameters, can be {@literal null}.
     * @return a new {@link Pageable} instance.
     */
    public static Pageable of(int pageNumber, int pageSize, Sort sort) {
        return new Pageable(pageNumber, pageSize, sort);
    }
}