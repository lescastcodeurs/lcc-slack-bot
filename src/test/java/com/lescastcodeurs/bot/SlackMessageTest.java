package com.lescastcodeurs.bot;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SlackMessageTest {

  @Test
  void nullTimestampIsReplacedWithNonNull() {
    SlackMessage slackMessage = new SlackMessage(null, null);

    assertNotNull(slackMessage.timestamp());
  }

  @Test
  void nullTextIsReplacedWithNonNull() {
    SlackMessage slackMessage = new SlackMessage(null, null);

    assertNotNull(slackMessage.text());
  }

  @Test
  void isLink() {
    assertTrue(new SlackMessage(null, "<https://www.google.fr/>").isLink());
    assertTrue(new SlackMessage(null, "<http://www.google.fr/>").isLink());

    assertFalse(new SlackMessage(null, "http://www.google.fr/").isLink());
    assertFalse(new SlackMessage(null, "whatever").isLink());
  }
}
