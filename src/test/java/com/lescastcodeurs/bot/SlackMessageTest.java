package com.lescastcodeurs.bot;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class SlackMessageTest {

  @Test
  void nullTimestampIsReplacedWithNonNull() {
    SlackMessage slackMessage = new SlackMessage(null, "msg", List.of());

    assertNotNull(slackMessage.timestamp());
  }

  @Test
  void nullTextIsReplacedWithNonNull() {
    SlackMessage slackMessage = new SlackMessage(
      "9999999999.999999",
      null,
      List.of()
    );

    assertNotNull(slackMessage.text());
  }

  @Test
  void nullRepliesIsReplacedWithEmptyList() {
    SlackMessage slackMessage = new SlackMessage(
      "9999999999.999999",
      "msg",
      null
    );

    assertNotNull(slackMessage.replies());
  }

  @Test
  void isLink() {
    assertTrue(
      new SlackMessage(null, "<https://www.google.fr/>", null).isLink()
    );
    assertTrue(
      new SlackMessage(null, "<http://www.google.fr/>", null).isLink()
    );

    assertFalse(new SlackMessage(null, "http://www.google.fr/", null).isLink());
    assertFalse(new SlackMessage(null, "whatever", null).isLink());
  }

  @Test
  void url() {
    String url = new SlackMessage(null, "<https://www.google.fr/>", null).url();

    assertEquals("https://www.google.fr/", url);
  }
}
