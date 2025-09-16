package dev.corusoft.eticketia.application.services;

import dev.corusoft.eticketia.domain.exceptions.PersistenceErrorException;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Interface for external storage services.
 */
public interface StorageService {

  /**
   * Upload a file to the storage service.
   *
   * @param basePath Path to the file
   * @param file     File to upload
   * @return Path to the file
   * @throws PersistenceErrorException File could not be uploaded
   */
  String uploadFile(String basePath, File file) throws PersistenceErrorException;

  /**
   * Upload a list of files to the given {@code baseRoute}.
   *
   * @param basePath Base path to upload the files to
   * @param files    List of files to upload
   * @return A map associating each file name with its corresponding URL in the storage
   * @throws PersistenceErrorException Files could not be uploaded
   */
  Map<String, String> uploadFiles(String basePath, List<File> files) throws PersistenceErrorException;

  /**
   * Downloads the file at the given path.
   *
   * @param path Path to the file
   * @return The downloaded file
   * @throws PersistenceErrorException File could not be downloaded
   */
  File downloadFile(String path) throws PersistenceErrorException;

  /**
   * Deletes the file at the given path.
   *
   * @param path Path to the file to be deleted.
   * @return True if the file was deleted, false otherwise
   * @throws PersistenceErrorException File could not be deleted
   */
  boolean deleteFile(String path) throws PersistenceErrorException;

}
