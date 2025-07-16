package dev.corusoft.eticketia.domain.repositories;

import lombok.Getter;

import java.util.List;

/**
 * A page is a sublist of a list of objects. It allows navigating to previous and next pages.
 *
 * @param <T> the type of which the page consists.
 */
@Getter
public class Page<T> {

    /**
     * The content of this page.
     */
    private final List<T> content;
    /**
     * The number of the current page, zero-based.
     */
    private final int pageNumber;
    /**
     * The size of the page.
     */
    private final int pageSize;
    /**
     * The total amount of elements available.
     */
    private final long totalElements;
    /**
     * The total number of pages.
     */
    private final int totalPages;
    /**
     * Whether this is the last page.
     */
    private final boolean isLast;
    /**
     * Whether this is the first page.
     */
    private final boolean isFirst;

    /**
     * Creates a new {@link Page} with the given content and pagination information.
     *
     * @param content the content of this page, must not be {@literal null}.
     * @param pageNumber the number of the current page.
     * @param pageSize the size of the page.
     * @param totalElements the total amount of elements available.
     */
    public Page(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / (double) pageSize) : 0;
        this.isFirst = pageNumber == 0;
        this.isLast = (pageNumber + 1) >= totalPages;
    }
}