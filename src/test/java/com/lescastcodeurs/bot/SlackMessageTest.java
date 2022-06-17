package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.SlackMessage.DEFAULT_TS;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    SlackMessage slackMessage = new SlackMessage(DEFAULT_TS, null, List.of());

    assertNotNull(slackMessage.text());
  }

  @Test
  void nullRepliesIsReplacedWithEmptyList() {
    SlackMessage slackMessage = new SlackMessage(DEFAULT_TS, "msg", null);

    assertNotNull(slackMessage.replies());
  }
}
