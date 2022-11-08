package com.lescastcodeurs.bot.slack;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class SlackMessageTest {

  @Test
  void nullTimestampIsReplacedWithNonNull() {
    SlackMessage msg = new SlackReply(Messages.of(null, "text", List.of(), null, null));

    assertNotNull(msg.timestamp());
  }

  @Test
  void nullTextIsReplacedWithNonNull() {
    SlackMessage msg = new SlackReply(Messages.of(null));

    assertNotNull(msg.text());
  }

  @Test
  void nullRepliesIsReplacedWithEmptyList() {
    SlackThread thread = new SlackThread(Messages.of("text"), null);

    assertNotNull(thread.replies());
    assertTrue(thread.replies().isEmpty());
  }

  @Test
  void rawLinksAreProperlyTransformedToMarkdownLinks() {
    SlackMessage msg = new SlackReply(Messages.of("<https://lescastcodeurs.com/>"));

    assertEquals("[https://lescastcodeurs.com/](https://lescastcodeurs.com/)", msg.asMarkdown());
  }

  @Test
  void titledLinksAreProperlyTransformedToMarkdownLinks() {
    SlackMessage msg =
        new SlackReply(Messages.of("<https://lescastcodeurs.com/|Le podcast Java en Français>"));

    assertEquals("[Le podcast Java en Français](https://lescastcodeurs.com/)", msg.asMarkdown());
  }

  @Test
  void boldIsProperlyTransformed() {
    SlackMessage msg = new SlackReply(Messages.of("*some bold text*"));

    assertEquals("**some bold text**", msg.asMarkdown());
  }

  @Test
  void listIsProperlyTransformed() {
    SlackMessage msg =
        new SlackReply(Messages.of("""
        • element 1
        • element 2
        """));

    assertEquals("""
      - element 1
      - element 2
      """, msg.asMarkdown());
  }

  @Test
  void sublistIsProperlyTransformed() {
    SlackMessage msg =
        new SlackReply(
            Messages.of(
                """
          • element 1
            ◦ subelement 1
            ◦ subelement 2
          """));

    assertEquals(
        """
        - element 1
          - subelement 1
          - subelement 2
        """,
        msg.asMarkdown());
  }

  @Test
  void blockquoteIsProperlyTransformed() {
    SlackMessage msg =
        new SlackReply(
            Messages.of(
                """
      La classe américaine, L’indien à Hugues. :
      &gt; J’aimerais bien que tu restes.
      &gt; On va manger des chips. Tu entends ? Des chips !
      &gt; C’est tout ce que ça te fait quand je te dis qu’on va manger des chips ?
      """));

    assertEquals(
        """
      La classe américaine, L’indien à Hugues. :
      > J’aimerais bien que tu restes.
      > On va manger des chips. Tu entends ? Des chips !
      > C’est tout ce que ça te fait quand je te dis qu’on va manger des chips ?
      """,
        msg.asMarkdown());
  }

  @Test
  void complexMessageIsProperlyTransformedToMarkdown() {
    SlackMessage msg =
        new SlackReply(
            Messages.of(
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
          """));

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
        msg.asMarkdown());
  }

  @Test
  void recognizeUserMentionAbsence() {
    SlackMessage message = new SlackReply(Messages.of("test message"));

    assertFalse(message.hasMention());
  }

  @Test
  void recognizeUserMentionInMainMessage() {
    SlackMessage message = new SlackReply(Messages.of("test <@U1N352J01>"));

    assertTrue(message.hasMention());
  }

  @Test
  void recognizeChannelMentionInMainMessage() {
    SlackMessage message = new SlackReply(Messages.of("test <!channel>"));

    assertTrue(message.hasMention());
  }

  @Test
  void recognizeUserMentionMessageWithLinks() {
    SlackMessage message =
        new SlackReply(
            Messages.of(
                """
       c’est mixé <@V6A4QLCNA> cc <@YAP44U4QZ>
       • Je ne suis toujours pas assez doué pour le mégaphone
       • L’audio <https://lescastcodeurs.com/|Le podcast Java en Français>
       • Les notes <https://lescastcodeurs.com/>
      """));

    assertTrue(message.hasMention());
    assertTrue(message.hasLink());
  }

  @Test
  void recognizeUserMentionInMultilineMessage() {
    SlackMessage message =
        new SlackReply(
            Messages.of(
                """
 c’est mixé
 • Je ne suis toujours pas assez doué pour le mégaphone
 • L’audio <https://lescastcodeurs.com/|Le podcast Java en Français>
 • Les notes <https://lescastcodeurs.com/>

 cc <@YAP44U4QZ>
"""));

    assertTrue(message.hasMention());
    assertTrue(message.hasLink());
  }

  @Test
  void recognizeLinkAbsence() {
    SlackMessage message = new SlackReply(Messages.of("test message"));

    assertFalse(message.hasLink());
  }

  @Test
  void recognizeLinkAbsence2() {
    SlackMessage message = new SlackReply(Messages.of("test <http://message"));

    assertFalse(message.hasLink());
  }

  @Test
  void recognizeSimpleLinkPresence() {
    SlackMessage message = new SlackReply(Messages.of("test <https://lescastcodeurs.com/>"));

    assertTrue(message.hasLink());
  }

  @Test
  void recognizeTitledLinkPresence() {
    SlackMessage message =
        new SlackReply(
            Messages.of("test <https://lescastcodeurs.com/|Le podcast Java en Français>"));

    assertTrue(message.hasLink());
  }

  @Test
  void recognizeLinkPresenceInMultilineMessages() {
    SlackMessage message =
        new SlackReply(
            Messages.of(
                """
 c’est mixé
 • Je ne suis toujours pas assez doué pour le mégaphone
 • L’audio <https://lescastcodeurs.com/|Le podcast Java en Français>
 • Les notes <https://lescastcodeurs.com/>
"""));

    assertTrue(message.hasLink());
  }
}
