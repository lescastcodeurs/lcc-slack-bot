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
  void isShowNoteEntry() {
    assertTrue(
      new SlackMessage(null, "<https://www.google.fr/>", null).isShowNoteEntry()
    );
    assertTrue(
      new SlackMessage(null, "<http://www.google.fr/>", null).isShowNoteEntry()
    );

    assertFalse(
      new SlackMessage(null, "http://www.google.fr/", null).isShowNoteEntry()
    );
    assertFalse(new SlackMessage(null, "whatever", null).isShowNoteEntry());
  }

  @Test
  void asShowNotesEntry() {
    SlackMessage message = new SlackMessage(
      null,
      "<https://www.google.fr>",
      List.of(" • note 1\n• \tnote 2\t\n• note 3 \n")
    );

    SlackMessage result = message.asShowNotesEntry();

    assertEquals("https://www.google.fr", result.text());
    assertEquals(List.of("note 1", "note 2", "note 3"), result.replies());
  }
}
