package dev.corusoft.eticketia.domain.repositories;

import dev.corusoft.eticketia.domain.exceptions.PersistenceErrorException;
import java.util.Optional;

/**
 * Base repository interface providing generic CRUD operations. This is designed to be independent
 * of the underlying persistence technology.
 *
 * @param <T>  the domain type the repository manages
 * @param <ID> the type of the id of the domain type the repository manages
 */
public interface Repository<T, ID> {

  /**
   * Saves a given entity.
   *
   * @param entity must not be {@literal null}.
   * @param <S>    the type of the entity
   * @return the saved entity; will never be {@literal null}.
   */
  <S extends T> S save(S entity) throws PersistenceErrorException;

  /**
   * Saves all given entities.
   *
   * @param entities must not be {@literal null} nor contain {@literal null}.
   * @param <S>      the type of the entity
   * @return the saved entities; will never be {@literal null}.
   */
  <S extends T> Iterable<S> saveAll(Iterable<S> entities) throws PersistenceErrorException;

  /**
   * Retrieves an entity by its id.
   *
   * @param id must not be {@literal null}.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   */
  Optional<T> findById(ID id) throws PersistenceErrorException;

  /**
   * Returns whether an entity with the given id exists.
   *
   * @param id must not be {@literal null}.
   * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
   */
  boolean existsById(ID id) throws PersistenceErrorException;

  /**
   * Returns all instances of the type.
   *
   * @return all entities
   */
  Iterable<T> findAll() throws PersistenceErrorException;

  /**
   * Returns the number of entities available.
   *
   * @return the number of entities.
   */
  long count() throws PersistenceErrorException;

  /**
   * Deletes the entity with the given id.
   *
   * @param id must not be {@literal null}.
   */
  void deleteById(ID id);

  /**
   * Deletes a given entity.
   *
   * @param entity must not be {@literal null}.
   */
  void delete(T entity);

  /**
   * Deletes all instances of the type {@code T} with the given IDs.
   *
   * @param ids must not be {@literal null}. Must not contain {@literal null} elements.
   */
  void deleteAllById(Iterable<ID> ids) throws PersistenceErrorException;

  /**
   * Deletes the given entities.
   *
   * @param entities must not be {@literal null}. Must not contain {@literal null} elements.
   */
  void deleteAll(Iterable<? extends T> entities) throws PersistenceErrorException;

}
