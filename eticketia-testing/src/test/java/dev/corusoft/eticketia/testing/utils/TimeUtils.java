package dev.corusoft.eticketia.testing.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Utility class to manipulate time related values.
 */
public class TimeUtils {
  private static final ZoneOffset UTC_ZONE_OFFSET = ZoneOffset.UTC;

  /**
   * Return the current time in UTC milliseconds.
   */
  public static long currentUtcMilliseconds() {
    return toUtcMilliseconds(LocalDateTime.now());
  }

  /**
   * Return the given {@link LocalDateTime} to UTC milliseconds.
   */
  public static long toUtcMilliseconds(LocalDateTime localDateTime) {
    long epochMilliseconds = localDateTime.toInstant(UTC_ZONE_OFFSET).toEpochMilli();
    return toUtcMilliseconds(epochMilliseconds);
  }

  /**
   * Convert the given milliseconds timestamp to UTC timestamp.
   */
  public static long toUtcMilliseconds(long milliseconds) {
    return Instant.ofEpochMilli(milliseconds)
        .atOffset(UTC_ZONE_OFFSET)
        .toInstant()
        .toEpochMilli();
  }

}
