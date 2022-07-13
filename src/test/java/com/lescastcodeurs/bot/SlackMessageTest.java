package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.SlackMessage.DEFAULT_TS;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

  @Test
  void rawLinksAreProperlyTransformedToMarkdownLinks() {
    SlackMessage message = new SlackMessage(DEFAULT_TS, "<https://lescastcodeurs.com/>", null);

    assertEquals(
        "[https://lescastcodeurs.com/](https://lescastcodeurs.com/)", message.asMarkdown());
  }

  @Test
  void titledLinksAreProperlyTransformedToMarkdownLinks() {
    SlackMessage message =
        new SlackMessage(
            DEFAULT_TS, "<https://lescastcodeurs.com/|Le podcast Java en Français>", null);

    assertEquals(
        "[Le podcast Java en Français](https://lescastcodeurs.com/)", message.asMarkdown());
  }

  @Test
  void boldIsProperlyTransformed() {
    SlackMessage message = new SlackMessage(DEFAULT_TS, "*some bold text*", null);

    assertEquals("**some bold text**", message.asMarkdown());
  }

  @Test
  void listIsProperlyTransformed() {
    SlackMessage message =
        new SlackMessage(DEFAULT_TS, """
        • element 1
        • element 2
        """, null);

    assertEquals("""
      - element 1
      - element 2
      """, message.asMarkdown());
  }

  @Test
  void sublistIsProperlyTransformed() {
    SlackMessage message =
        new SlackMessage(
            DEFAULT_TS,
            """
          • element 1
            ◦ subelement 1
            ◦ subelement 2
          """,
            null);

    assertEquals(
        """
        - element 1
          - subelement 1
          - subelement 2
        """,
        message.asMarkdown());
  }

  @Test
  void complexMessageIsProperlyTransformedToMarkdown() {
    SlackMessage message =
        new SlackMessage(
            DEFAULT_TS,
            """
          • <https://lescastcodeurs.com/|Le podcast Java en Français>
            ◦ something to say on this link ?
          • <https://lescastcodeurs.com/>
          • <http://example.com/blue+light%20blue?blue%2Blight+blu|I think I blue myself!>
          • Formatting:
           ◦ *some bold text*
           ◦ _some italic text_
           ◦ ~some striked text~
           ◦ `some code`
          """,
            null);

    assertEquals(
        """
        - [Le podcast Java en Français](https://lescastcodeurs.com/)
          - something to say on this link ?
        - [https://lescastcodeurs.com/](https://lescastcodeurs.com/)
        - [I think I blue myself!](http://example.com/blue+light%20blue?blue%2Blight+blu)
        - Formatting:
          - **some bold text**
          - _some italic text_
          - ~some striked text~
          - `some code`
        """,
        message.asMarkdown());
  }
}
