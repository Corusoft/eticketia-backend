package dev.corusoft.eticketia.infrastructure.services.firebase;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.firebase.cloud.StorageClient;
import dev.corusoft.eticketia.application.services.StorageService;
import dev.corusoft.eticketia.domain.exceptions.PersistenceErrorException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Log4j2
@RequiredArgsConstructor
public class FirebaseStorageServiceImpl implements StorageService {

  private static final String STORAGE_PROTOCOL_PREFIX = "gs://";
  private static final Path TEMPORAL_DOWNLOADS_PATH = Files.createTempFile()

  private final StorageClient storageClient;

  @Override
  public String uploadFile(String basePath, File file) throws PersistenceErrorException {
    String bucketName = storageClient.bucket().getName();
    String fileName = file.getName();
    String blobName = "%s/%s".formatted(basePath, fileName);
    BlobId blobId = BlobId.of(bucketName, blobName);

    Path filePath = file.toPath();
    try (InputStream fs = Files.newInputStream(filePath)) {
      String contentType = Files.probeContentType(filePath);
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
          .setContentType(contentType)
          .build();

      log.debug("Uploading file '{}' to Firebase Storage bucket '{}' as '{}'", fileName, bucketName, blobName);
      storageClient.bucket().create(blobName, fs, blobInfo.getContentType());
      String uploadedFileUri = "%s%s/%s".formatted(STORAGE_PROTOCOL_PREFIX, bucketName, blobName);

      log.debug("File '{}' uploaded to path '{}'", fileName, uploadedFileUri);
      return uploadedFileUri;
    } catch (IOException e) {
      String message = "Failed to upload file '%s' to Firebase Storage bucket".formatted(fileName);
      log.error(message);
      throw new PersistenceErrorException(message, e);
    }
  }

  @Override
  public Map<String, String> uploadFiles(String basePath, List<File> files) throws PersistenceErrorException {
    Map<String, String> uploadedFilesMap = new HashMap<>(files.size());
    for (File file : files) {
      int i = 0;
      log.debug("Uploading file {}/{} '{}' to Firebase Storage", ++i, files.size(), file.getName());
      String uploadedFileUri = uploadFile(basePath, file);
      uploadedFilesMap.put(file.getName(), uploadedFileUri);
    }

    return uploadedFilesMap;
  }

  @Override
  public File downloadFile(String path) throws PersistenceErrorException {
    String bucketName = storageClient.bucket().getName();
    Blob blob = storageClient.bucket().get(path);

    boolean existsFileInPath = (blob == null) && blob.exists();
    if (!existsFileInPath) {
      String message = "File not found in Firebase Storage at path: %s".formatted(path);
      log.warn(message);
      throw new PersistenceErrorException(message);
    }

    try {
      Path tempFile
    }
  }

  @Override
  public boolean deleteFile(String path) throws PersistenceErrorException {
    return false;
  }
}
