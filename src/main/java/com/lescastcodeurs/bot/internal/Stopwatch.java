package com.lescastcodeurs.bot.internal;

import static java.util.concurrent.TimeUnit.*;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * An object that accurately measures <i>elapsed time</i>: the measured duration between two
 * successive readings of "now" in the same process.
 *
 * <p>Inspired by Guava's {@code StopWatch}.
 */
public final class Stopwatch {
  private final long start;

  public Stopwatch() {
    this.start = System.nanoTime();
  }

  private long elapsedNanos() {
    return System.nanoTime() - start;
  }

  /** Returns a string representation of the current elapsed time. */
  @Override
  public String toString() {
    long nanos = elapsedNanos();

    TimeUnit unit = chooseUnit(nanos);
    double value = (double) nanos / NANOSECONDS.convert(1, unit);

    return String.format(Locale.ROOT, "%.4g", value) + " " + abbreviate(unit);
  }

  private static TimeUnit chooseUnit(long nanos) {
    if (DAYS.convert(nanos, NANOSECONDS) > 0) {
      return DAYS;
    } else if (HOURS.convert(nanos, NANOSECONDS) > 0) {
      return HOURS;
    } else if (MINUTES.convert(nanos, NANOSECONDS) > 0) {
      return MINUTES;
    } else if (SECONDS.convert(nanos, NANOSECONDS) > 0) {
      return SECONDS;
    } else if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) {
      return MILLISECONDS;
    } else if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0) {
      return MICROSECONDS;
    }
    return NANOSECONDS;
  }

  private static String abbreviate(TimeUnit unit) {
    return switch (unit) {
      case NANOSECONDS -> "ns";
      case MICROSECONDS -> "\u03bcs"; // μs
      case MILLISECONDS -> "ms";
      case SECONDS -> "s";
      case MINUTES -> "min";
      case HOURS -> "h";
      case DAYS -> "d";
    };
  }
}
