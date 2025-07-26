package dev.corusoft.eticketia.infrastructure.repositories;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.AggregateQuerySnapshot;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.Query.Direction;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import dev.corusoft.eticketia.domain.exceptions.PersistenceErrorException;
import dev.corusoft.eticketia.domain.repositories.Identifiable;
import dev.corusoft.eticketia.domain.repositories.Page;
import dev.corusoft.eticketia.domain.repositories.Pageable;
import dev.corusoft.eticketia.domain.repositories.PagingAndSortingRepository;
import dev.corusoft.eticketia.domain.repositories.Repository;
import dev.corusoft.eticketia.domain.repositories.Sort;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Generic implementation for {@link Repository} methods.
 *
 * @param <T>  Entity type, which must be {@link Identifiable}
 * @param <ID> Type of the entity's identifier
 */
@Log4j2
@RequiredArgsConstructor
public abstract class AbstractFirestoreRepository<T extends Identifiable> implements
    PagingAndSortingRepository<T> {

  private final Class<T> entityType;
  private final String collectionName;
  @Autowired
  private Firestore firestore;

  @Override
  public <S extends T> S save(S entity) throws PersistenceErrorException {
    DocumentReference docRef;
    try {
      // Assign a new ID to the entity if it doesn't have one
      docRef = assignIdToEntity(entity);
      docRef.set(entity).get();
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      String message = "Error saving entity in Firestore";
      log.error(message, e);
      throw new PersistenceErrorException(e, message);
    }

    return entity;
  }

  private <S extends T> DocumentReference assignIdToEntity(S entity) {
    DocumentReference documentReference;
    if (entity.getId() == null) {
      documentReference = getCollection().document();
      entity.setId(documentReference.getId());
    } else {
      documentReference = getCollection().document(getEntityId(entity));
    }

    return documentReference;
  }

  /**
   * Gets the collection reference.
   */
  protected CollectionReference getCollection() {
    return firestore.collection(this.collectionName);
  }

  /**
   * Get the id attribute for the given entity.
   */
  protected <S extends T> String getEntityId(S entity) {
    if (entity.getId() == null) {
      return null;
    }

    return entity.getId();
  }

  // region Override Repository

  @Override
  public <S extends T> Iterable<S> saveAll(Iterable<S> entities) throws PersistenceErrorException {
    WriteBatch batch = firestore.batch();
    for (S entity : entities) {
      DocumentReference docRef = assignIdToEntity(entity);
      batch.set(docRef, entity);
    }

    try {
      batch.commit().get();
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      String message = "Error batch saving entities in Firestore";
      log.error(message, e);
      throw new PersistenceErrorException(e, message);
    }

    return entities;
  }

  @Override
  public Optional<T> findById(String id) throws PersistenceErrorException {
    try {
      ApiFuture<DocumentSnapshot> documentSnapshotFuture = getCollection().document(id).get();
      DocumentSnapshot snapshot = documentSnapshotFuture.get();
      if (!snapshot.exists()) {
        return Optional.empty();
      }

      return Optional.ofNullable(snapshot.toObject(this.entityType));
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      String message = "Error finding entity by ID in Firestore";
      log.error(message, e);
      throw new PersistenceErrorException(e, message);
    }
  }

  @Override
  public boolean existsById(String id) throws PersistenceErrorException {
    try {
      ApiFuture<DocumentSnapshot> documentSnapshotFuture = getCollection().document(id).get();
      DocumentSnapshot document = documentSnapshotFuture.get();

      return document.exists();
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      String message = "Error checking existence of entity by ID in Firestore";
      log.error(message, e);
      throw new PersistenceErrorException(e, message);
    }
  }

  @Override
  public Iterable<T> findAll() throws PersistenceErrorException {
    try {
      ApiFuture<QuerySnapshot> querySnapshotFuture = getCollection().get();
      QuerySnapshot document = querySnapshotFuture.get();

      return document.toObjects(this.entityType);
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      String message = "Error finding all entities in Firestore";
      log.error(message, e);
      throw new PersistenceErrorException(e, message);
    }
  }

  @Override
  public long count() throws PersistenceErrorException {
    try {
      ApiFuture<AggregateQuerySnapshot> countFuture = getCollection().count().get();

      return countFuture.get().getCount();
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      String message = "Error counting entities in Firestore";
      log.error(message, e);
      throw new PersistenceErrorException(e, message);
    }
  }

  @Override
  public void deleteById(String id) {
    getCollection()
        .document(id)
        // Note: This operation only deletes the document. Any subcollections within this document will NOT be deleted.
        .delete();
  }

  @Override
  public void delete(T entity) {
    deleteById(entity.getId());
  }

  @Override
  public void deleteAllById(Iterable<String> ids) throws PersistenceErrorException {
    if (ids == null || !ids.iterator().hasNext()) {
      return;
    }
    WriteBatch batch = firestore.batch();
    for (String id : ids) {
      if (id != null) {
        batch.delete(getCollection().document(id));
      }
    }

    try {
      batch.commit().get();
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      String message = "Error batch deleting entities by ID in Firestore";
      log.error(message, e);
      throw new PersistenceErrorException(e, message);
    }
  }

  @Override
  public void deleteAll(Iterable<? extends T> entities) throws PersistenceErrorException {
    if (entities == null || !entities.iterator().hasNext()) {
      return;
    }
    WriteBatch batch = firestore.batch();
    for (T entity : entities) {
      if (entity != null && entity.getId() != null) {
        batch.delete(getCollection().document(getEntityId(entity)));
      }
    }
    try {
      batch.commit().get();
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      String message = "Error batch deleting entities in Firestore";
      log.error(message, e);
      throw new PersistenceErrorException(e, message);
    }
  }

  @Override
  public Iterable<T> findAll(Sort sort) throws PersistenceErrorException {
    Direction direction = getDirection(sort);

    try {
      ApiFuture<QuerySnapshot> querySnapshotFuture = getCollection()
          .orderBy(sort.getProperty(), direction)
          .get();

      return querySnapshotFuture.get().toObjects(this.entityType);
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      String message = "Error finding and sorting all entities in Firestore";
      log.error(message, e);
      throw new PersistenceErrorException(e, message);
    }
  }

  // endregion Override Repository

  // region Override PagingAndSortingRepository

  @Override
  public Page<T> findAll(Pageable pageable) throws PersistenceErrorException {
    // Firestore requires two queries: one for counting and other for results
    try {
      // Build Future for counting results
      ApiFuture<AggregateQuerySnapshot> countFuture = getCollection().count().get();

      // Build Future for getting the results
      Query contentQuery = getCollection();
      // Sort if required
      if (pageable.getSort() != null) {
        Direction direction = getDirection(pageable.getSort());
        contentQuery = contentQuery.orderBy(pageable.getSort().getProperty(), direction);
      }
      int offset = pageable.getPageNumber() * pageable.getPageSize();
      contentQuery = contentQuery.limit(pageable.getPageSize()).offset(offset);
      ApiFuture<QuerySnapshot> contentFuture = contentQuery.get();

      // Execute and await for the futures to complete
      List<Object> results = ApiFutures.allAsList(List.of(countFuture, contentFuture)).get();

      // 4. Extract and process the results from the completed futures.
      AggregateQuerySnapshot countSnapshot = (AggregateQuerySnapshot) results.get(0);
      QuerySnapshot contentSnapshot = (QuerySnapshot) results.get(1);

      long count = countSnapshot.getCount();
      List<T> content = contentSnapshot.toObjects(this.entityType);

      return new Page<>(content, pageable.getPageNumber(), pageable.getPageSize(), count);
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      String message = "Error finding and paging all entities in Firestore";
      log.error(message, e);
      throw new PersistenceErrorException(e, message);
    }
  }

  private static Direction getDirection(Sort sort) {
    return (sort.getDirection() == Sort.Direction.ASC)
        ? Direction.ASCENDING
        : Direction.DESCENDING;
  }

  // endregion Override PagingAndSortingRepository


}
