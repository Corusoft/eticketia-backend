package dev.corusoft.eticketia.infrastructure.repositories;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.Query.Direction;
import dev.corusoft.eticketia.domain.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Generic implementation for {@link Repository} methods.
 *
 * @param <T>  Entity type, which must be {@link Identifiable}
 * @param <ID> Type of the entity's identifier
 */
@Log4j2
@RequiredArgsConstructor
public abstract class AbstractFirestoreRepository<T extends Identifiable<ID>, ID extends Serializable> implements PagingAndSortingRepository<T, ID> {
    private final Firestore firestore;
    private final Class<T> entityType;
    private final String collectionName;

    private static Direction getDirection(Sort sort) {
        Direction direction = sort.getDirection() == Sort.Direction.ASC ? Direction.ASCENDING : Direction.DESCENDING;
        return direction;
    }

    /**
     * Gets the collection reference.
     */
    protected CollectionReference getCollection() {
        return firestore.collection(this.collectionName);
    }

    protected <S extends T> String getEntityId(S entity) {
        return entity.getId().toString();
    }

    // region Override Repository

    @Override
    public <S extends T> S save(S entity) {
        getCollection()
                .document(getEntityId(entity))
                .set(entity);

        return entity;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        WriteBatch batch = firestore.batch();
        for (S entity : entities) {
            DocumentReference docRef = getCollection().document(getEntityId(entity));
            batch.set(docRef, entity);
        }

        try {
            batch.commit().get();
        } catch (InterruptedException | ExecutionException e) {
            String message = "Error batch saving entities in Firestore";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }

        return entities;
    }

    @Override
    public Optional<T> findById(ID id) {
        try {
            ApiFuture<DocumentSnapshot> documentSnapshotFuture = getCollection().document(id.toString()).get();
            DocumentSnapshot snapshot = documentSnapshotFuture.get();

            return snapshot.exists() ? Optional.ofNullable(snapshot.toObject(this.entityType)) : Optional.empty();

        } catch (InterruptedException | ExecutionException e) {
            String message = "Error finding entity by ID in Firestore";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public boolean existsById(ID id) {
        try {
            ApiFuture<DocumentSnapshot> documentSnapshotFuture = getCollection().document(id.toString()).get();
            DocumentSnapshot document = documentSnapshotFuture.get();

            return document.exists();
        } catch (InterruptedException | ExecutionException e) {
            String message = "Error checking existence of entity by ID in Firestore";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public long count() {
        try {
            ApiFuture<AggregateQuerySnapshot> countFuture = getCollection().count().get();

            return countFuture.get().getCount();
        } catch (InterruptedException | ExecutionException e) {
            String message = "Error counting entities in Firestore";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public Iterable<T> findAll() {
        try {
            ApiFuture<QuerySnapshot> querySnapshotFuture = getCollection().get();
            QuerySnapshot document = querySnapshotFuture.get();

            return document.toObjects(this.entityType);
        } catch (InterruptedException | ExecutionException e) {
            String message = "Error finding all entities in Firestore";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public void deleteById(ID id) {
        getCollection()
                .document(id.toString())
                // Note: This operation only deletes the document. Any subcollections within this document will NOT be deleted.
                .delete();
    }

    @Override
    public void delete(T entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        if (ids == null || !ids.iterator().hasNext()) {
            return;
        }
        WriteBatch batch = firestore.batch();
        for (ID id : ids) {
            if (id != null) {
                batch.delete(getCollection().document(id.toString()));
            }
        }

        try {
            batch.commit().get();
        } catch (InterruptedException | ExecutionException e) {
            String message = "Error batch deleting entities by ID in Firestore";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
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
            String message = "Error batch deleting entities in Firestore";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    // endregion Override Repository

    // region Override PagingAndSortingRepository

    @Override
    public Page<T> findAll(Pageable pageable) {
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
            String message = "Error finding and paging all entities in Firestore";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        Direction direction = getDirection(sort);

        try {
            ApiFuture<QuerySnapshot> querySnapshotFuture = getCollection()
                    .orderBy(sort.getProperty(), direction)
                    .get();

            return querySnapshotFuture.get().toObjects(this.entityType);
        } catch (InterruptedException | ExecutionException e) {
            String message = "Error finding and sorting all entities in Firestore";
            log.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    // endregion Override PagingAndSortingRepository

}
