package com.lescastcodeurs.bot.conferences;

import static com.lescastcodeurs.bot.conferences.Conference.MAX_TIMESTAMP;
import static com.lescastcodeurs.bot.conferences.Conference.MIN_TIMESTAMP;
import static java.time.LocalTime.MIDNIGHT;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ConferenceTest {

  private static final String NAME = "Devoxx France";
  private static final String URL = "https://www.devoxx.fr/";
  private static final String LOC = "Paris (France)";
  private static final String MISC = "CFP ends on 2023-01-01";

  private static final LocalDate START = LocalDate.of(2023, 4, 12);
  private static final long START_STAMP = START.toEpochSecond(MIDNIGHT, UTC);
  private static final LocalDate END = LocalDate.of(2023, 4, 14);
  private static final long END_STAMP = END.toEpochSecond(MIDNIGHT, UTC);
  private static final long[] DATES = new long[] {START_STAMP, END_STAMP};

  @Test
  void nameCannotBeBlank() {
    assertFalse(new Conference("", URL, LOC, DATES, MISC).hasValidData());
  }

  @Test
  void urlCannotBeBlank() {
    assertFalse(new Conference(NAME, "", LOC, DATES, MISC).hasValidData());
  }

  @Test
  void datesCannotBeNull() {
    assertFalse(new Conference(NAME, URL, LOC, null, MISC).hasValidData());
  }

  @ParameterizedTest
  @MethodSource("invalidDates")
  void datesMustBeValid(long[] invalidDates) {
    assertFalse(new Conference(NAME, URL, LOC, invalidDates, MISC).hasValidData());
  }

  private static Stream<Arguments> invalidDates() {
    return Stream.of(
        of((Object) new long[] {}),
        of((Object) new long[] {MIN_TIMESTAMP}),
        of((Object) new long[] {MIN_TIMESTAMP, MAX_TIMESTAMP, MAX_TIMESTAMP}),
        // Disabled: https://github.com/scraly/developers-conferences-agenda/issues/319
        of((Object) new long[] {MAX_TIMESTAMP, MIN_TIMESTAMP}),
        of((Object) new long[] {MIN_TIMESTAMP - 1, MIN_TIMESTAMP}),
        of((Object) new long[] {MIN_TIMESTAMP, MAX_TIMESTAMP + 1}));
  }

  @Test
  void valid() {
    var conf = new Conference(NAME, URL, LOC, DATES, MISC);
    assertTrue(conf.hasValidData());
    assertTrue(conf.isOnOrAfter(END));
    assertFalse(conf.isOnOrAfter(END.plusDays(1)));
    assertTrue(conf.matchesAnyOf(List.of("Devoxx")));
    assertTrue(conf.matchesAnyOf(List.of("Paris")));
    assertTrue(conf.matchesAnyOf(List.of("(France)")));
  }
}
