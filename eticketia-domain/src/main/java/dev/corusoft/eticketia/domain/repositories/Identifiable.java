package dev.corusoft.eticketia.domain.repositories;

import java.io.Serializable;

/**
 * Interface for domain entities that have an identifier.
 *
 * @param <ID> the type of the identifier.
 */
public interface Identifiable<ID extends Serializable> {
    ID getId();
}