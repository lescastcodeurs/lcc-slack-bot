package com.lescastcodeurs.bot.internal;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class SlackUtils {

  private SlackUtils() {
    // prevent instantiation
  }

  public static LocalDateTime parseTimestamp(String ts, LocalDateTime defaultDateTime) {
    if (ts != null) {
      try {
        long unixTs = Long.parseLong(ts.split("\\.")[0]);
        return LocalDateTime.ofEpochSecond(unixTs, 0, ZoneOffset.UTC);
      } catch (NumberFormatException | DateTimeException e) {
        // ignore and return defaultDateTime
      }
    }

    return defaultDateTime;
  }
}
