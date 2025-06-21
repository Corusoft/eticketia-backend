package dev.corusoft.eticketia.infrastructure.api;

import java.time.LocalDateTime;

public record ApiResponse<T>(
    boolean success,
    LocalDateTime timestamp,
    T data
) {
  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  public static class Builder<T> {
    private boolean success;
    private LocalDateTime timestamp;
    private T data;

    public Builder<T> success(boolean success) {
      this.success = success;
      return this;
    }

    public Builder<T> timestamp(LocalDateTime timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder<T> data(T data) {
      this.data = data;
      return this;
    }

    public ApiResponse<T> build() {
      return new ApiResponse<>(success, timestamp, data);
    }
  }
}
