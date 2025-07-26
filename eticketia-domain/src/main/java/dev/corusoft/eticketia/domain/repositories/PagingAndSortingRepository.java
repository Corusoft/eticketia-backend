package dev.corusoft.eticketia.domain.repositories;

import dev.corusoft.eticketia.domain.exceptions.PersistenceErrorException;

/**
 * An extension of {@link Repository} to add sorting and pagination methods.
 *
 * @param <T>  the domain type the repository manages
 * @param <ID> the type of the id of the domain type the repository manages
 */
public interface PagingAndSortingRepository<T> extends Repository<T, String> {

  /**
   * Returns all entities sorted by the given options.
   */
  Iterable<T> findAll(Sort sort) throws PersistenceErrorException;

  /**
   * Returns a {@link Page} of entities meeting the paging restriction provided in the
   * {@link Pageable} object.
   */
  Page<T> findAll(Pageable pageable) throws PersistenceErrorException;

}