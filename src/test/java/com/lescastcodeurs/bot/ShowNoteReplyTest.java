package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.ShowNoteCategory.EXCLUDE;
import static com.lescastcodeurs.bot.ShowNoteCategory.INCLUDE;
import static com.lescastcodeurs.bot.ShowNoteReply.DEFAULT_ORDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lescastcodeurs.bot.slack.Messages;
import com.lescastcodeurs.bot.slack.SlackReply;
import java.util.List;
import org.junit.jupiter.api.Test;

class ShowNoteReplyTest {

  @Test
  void replyCannotBeNull() {
    assertThrows(NullPointerException.class, () -> new ShowNoteReply(null));
  }

  @Test
  void appMessagesAreExcluded() {
    SlackReply message = new SlackReply(Messages.of("test", null, "ABCD", null));
    var reply = new ShowNoteReply(message);

    assertFalse(reply.mustBeIncluded());
  }

  @Test
  void botMessagesAreExcluded() {
    SlackReply message = new SlackReply(Messages.of("test", null, null, "ABCD"));
    var reply = new ShowNoteReply(message);

    assertFalse(reply.mustBeIncluded());
  }

  @Test
  void messagesWithUserMentionAndWithoutExplicitIncludeAreExcluded() {
    SlackReply message = new SlackReply(Messages.of("test <@XXX>"));
    var reply = new ShowNoteReply(message);

    assertFalse(reply.mustBeIncluded());
  }

  @Test
  void messagesWithChannelMentionAndWithoutExplicitIncludeAreExcluded() {
    SlackReply message = new SlackReply(Messages.of("test <!channel>"));
    var reply = new ShowNoteReply(message);

    assertFalse(reply.mustBeIncluded());
  }

  @Test
  void messagesWithHereMentionAndWithoutExplicitIncludeAreExcluded() {
    SlackReply message = new SlackReply(Messages.of("test <!here>"));
    var reply = new ShowNoteReply(message);

    assertFalse(reply.mustBeIncluded());
  }

  @Test
  void explicitlyIncludedMessagesWithUserMentionAreIncluded() {
    SlackReply message = new SlackReply(Messages.of("test <@XXX>", List.of(INCLUDE.reaction())));
    var reply = new ShowNoteReply(message);

    assertTrue(reply.mustBeIncluded());
    assertEquals(List.of("- test <@XXX>"), reply.comments().toList());
  }

  @Test
  void messageWithoutMentionOrReactionAreIncluded() {
    SlackReply message = new SlackReply(Messages.of("test"));
    var reply = new ShowNoteReply(message);

    assertTrue(reply.mustBeIncluded());
    assertEquals(List.of("- test"), reply.comments().toList());
  }

  @Test
  void multilineMessagesAreSplit() {
    SlackReply message = new SlackReply(Messages.of("test 1\ntest 2"));
    var reply = new ShowNoteReply(message);

    assertEquals(List.of("- test 1", "- test 2"), reply.comments().toList());
  }

  @Test
  void plainListIsConvertedAsIs() {
    SlackReply message =
        new SlackReply(
            Messages.of("""
      - test 1
      - test 2
      - test 3
            """));
    var reply = new ShowNoteReply(message);

    assertEquals(List.of("- test 1", "- test 2", "- test 3"), reply.comments().toList());
  }

  @Test
  void plainListWithUnorderedSublistIsConvertedAsIs() {
    SlackReply message =
        new SlackReply(
            Messages.of(
                """
      - test 1
        - subtest 1
        - subtest 2
      - test 2
            """));
    var reply = new ShowNoteReply(message);

    assertEquals(
        List.of("- test 1", "  - subtest 1", "  - subtest 2", "- test 2"),
        reply.comments().toList());
  }

  @Test
  void plainListWithOrderedSublistIsConvertedAsIs() {
    SlackReply message =
        new SlackReply(
            Messages.of(
                """
      - test 1
        a. subtest 1
        b. subtest 2
      - test 2
      """));
    var reply = new ShowNoteReply(message);

    assertEquals(
        List.of("- test 1", "  1. subtest 1", "  1. subtest 2", "- test 2"),
        reply.comments().toList());
  }

  @Test
  void nonPlainListAreConvertedToSublist() {
    SlackReply message =
        new SlackReply(
            Messages.of("""
      Test:
      - test 1
      - test 2
      - test 3
      """));
    var reply = new ShowNoteReply(message);

    assertEquals(
        List.of("- Test:", "  - test 1", "  - test 2", "  - test 3"), reply.comments().toList());
  }

  @Test
  void nonPlainListAreConvertedToSublist2() {
    SlackReply message =
        new SlackReply(
            Messages.of(
                """
      Test A:
      - test A1
        a. test A12
      - test A2
      - test A3

      Test B:
      1. test B1
      2. test B2
      3. test B3
      """));
    var reply = new ShowNoteReply(message);

    assertEquals(
        List.of(
            "- Test A:",
            "  - test A1",
            "    1. test A12",
            "  - test A2",
            "  - test A3",
            "- Test B:",
            "  1. test B1",
            "  2. test B2",
            "  3. test B3"),
        reply.comments().toList());
  }

  @Test
  void excludeReactionAlwaysWins() {
    SlackReply message =
        new SlackReply(Messages.of("test", List.of(INCLUDE.reaction(), EXCLUDE.reaction())));
    var reply = new ShowNoteReply(message);

    assertFalse(reply.mustBeIncluded());
  }

  @Test
  void noOrderMeansDefaultOrder() {
    SlackReply message = new SlackReply(Messages.of("test", List.of(INCLUDE.reaction())));
    var reply = new ShowNoteReply(message);

    assertEquals(DEFAULT_ORDER, reply.order());
    assertTrue(reply.mustBeIncluded());
    assertEquals(List.of("- test"), reply.comments().toList());
  }

  @Test
  void lastOrderWins() {
    SlackReply message =
        new SlackReply(Messages.of("test", List.of("lcc_1", INCLUDE.reaction(), "lcc_9")));
    var reply = new ShowNoteReply(message);

    assertEquals(9, reply.order());
  }
}
