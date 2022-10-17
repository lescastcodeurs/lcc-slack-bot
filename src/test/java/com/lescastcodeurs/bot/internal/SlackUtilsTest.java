package com.lescastcodeurs.bot.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class SlackUtilsTest {

  @Test
  void parseNullSlackTimestamp() {
    assertNull(SlackUtils.parseTimestamp(null, null));
  }

  @Test
  void parseInvalidSlackTimestamp() {
    assertNull(SlackUtils.parseTimestamp(" ", null));
  }

  @Test
  void parseValidSlackTimestamp() {
    assertEquals(
        LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC), SlackUtils.parseTimestamp("0.0", null));
  }
}
