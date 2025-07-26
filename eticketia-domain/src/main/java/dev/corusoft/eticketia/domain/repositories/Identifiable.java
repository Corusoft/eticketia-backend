package dev.corusoft.eticketia.domain.repositories;

/**
 * Interface for domain entities that have an identifier.
 **/
public interface Identifiable {

  String getId();

  void setId(String id);
}