package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.ShowNote.DEFAULT_ORDER;
import static com.lescastcodeurs.bot.ShowNoteCategory.*;
import static org.junit.jupiter.api.Assertions.*;

import com.lescastcodeurs.bot.slack.Messages;
import com.lescastcodeurs.bot.slack.SlackThread;
import java.util.List;
import org.junit.jupiter.api.Test;

class ShowNoteTest {

  @Test
  void threadCannotBeNull() {
    assertThrows(NullPointerException.class, () -> new ShowNote(null));
  }

  @Test
  void appMessagesAreExcluded() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://lescastcodeurs.com/>", null, "ABCD", null), List.of());
    var note = new ShowNote(message);

    assertFalse(note.mustBeIncluded());
  }

  @Test
  void botMessagesAreExcluded() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://lescastcodeurs.com/>", null, null, "ABCD"), List.of());
    var note = new ShowNote(message);

    assertFalse(note.mustBeIncluded());
  }

  @Test
  void messagesWithoutLinkAndWithoutCategoryAreExcluded() {
    SlackThread message = new SlackThread(Messages.of("test", null), List.of());
    var note = new ShowNote(message);

    assertNull(note.category());
    assertFalse(note.mustBeIncluded());
  }

  @Test
  void messagesWithoutLinkAndWithCategoryAreIncluded() {
    SlackThread message =
        new SlackThread(Messages.of("test", List.of(CLOUD.reaction())), List.of());
    var note = new ShowNote(message);

    assertEquals(CLOUD, note.category());
    assertTrue(note.mustBeIncluded());
    assertEquals("test", note.text());
  }

  @Test
  void messagesWithLinkAndWithoutCategoryAreIncluded() {
    SlackThread message = new SlackThread(Messages.of("<https://test.io>"), List.of());
    var note = new ShowNote(message);

    assertNull(note.category());
    assertTrue(note.mustBeIncluded());
    assertEquals("[https://test.io](https://test.io)", note.text());
  }

  @Test
  void messagesWithLinkAndWithUserMentionAndWithoutCategoryAreExcluded() {
    SlackThread message = new SlackThread(Messages.of("<http://test.io/> <@XXX>"), List.of());
    var note = new ShowNote(message);

    assertNull(note.category());
    assertFalse(note.mustBeIncluded());
  }

  @Test
  void messagesWithLinkAndWithChannelMentionAndWithoutCategoryAreExcluded() {
    SlackThread message = new SlackThread(Messages.of("<http://test.io/> <!channel>"), List.of());
    var note = new ShowNote(message);

    assertNull(note.category());
    assertFalse(note.mustBeIncluded());
  }

  @Test
  void messagesWithLinkAndWithHereMentionAndWithoutCategoryAreExcluded() {
    SlackThread message = new SlackThread(Messages.of("<http://test.io/> <!here>"), List.of());
    var note = new ShowNote(message);

    assertNull(note.category());
    assertFalse(note.mustBeIncluded());
  }

  @Test
  void messagesWithoutLinkAndWithUserMentionAndWithCategoryAreIncluded() {
    SlackThread message =
        new SlackThread(
            Messages.of("<http://test.io/> <@XXX>", List.of(CLOUD.reaction())), List.of());
    var note = new ShowNote(message);

    assertEquals(CLOUD, note.category());
    assertTrue(note.mustBeIncluded());
  }

  @Test
  void messageWithLinkAndWithCategoryIsShowNote() {
    SlackThread message =
        new SlackThread(Messages.of("<https://test.io>", List.of(CLOUD.reaction())), List.of());
    var note = new ShowNote(message);

    assertEquals(CLOUD, note.category());
    assertTrue(note.mustBeIncluded());
    assertEquals("[https://test.io](https://test.io)", note.text());
  }

  @Test
  void messageWithLinkAndExplicitlyIgnoredIsNotShowNote() {
    SlackThread message =
        new SlackThread(Messages.of("<https://test.io>", List.of(EXCLUDE.reaction())), List.of());
    var note = new ShowNote(message);

    assertEquals(EXCLUDE, note.category());
    assertFalse(note.mustBeIncluded());
  }

  @Test
  void noOrderMeansDefaultOrder() {
    SlackThread message =
        new SlackThread(Messages.of("<https://test.io>", List.of(INCLUDE.reaction())), List.of());
    var note = new ShowNote(message);

    assertEquals(DEFAULT_ORDER, note.order());
    assertTrue(note.mustBeIncluded());
  }

  @Test
  void lastOrderWins() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://test.io>", List.of("lcc_1", INCLUDE.reaction(), "lcc_9")),
            List.of());
    var note = new ShowNote(message);

    assertEquals(9, note.order());
    assertTrue(note.mustBeIncluded());
  }

  @Test
  void lastReactionWins1() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://test.io>", List.of(DATA.reaction(), CLOUD.reaction())),
            List.of());
    var note = new ShowNote(message);

    assertEquals(CLOUD, note.category());
    assertTrue(note.mustBeIncluded());
  }

  @Test
  void lastReactionWins2() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://test.io>", List.of(CLOUD.reaction(), DATA.reaction())),
            List.of());
    var note = new ShowNote(message);

    assertEquals(DATA, note.category());
    assertTrue(note.mustBeIncluded());
  }

  @Test
  void excludeReactionAlwaysWins() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://test.io>", List.of(EXCLUDE.reaction(), CLOUD.reaction())),
            List.of());
    var note = new ShowNote(message);

    assertEquals(CLOUD, note.category());
    assertFalse(note.mustBeIncluded());
  }

  @Test
  void repliesAreProperlyTransformedToList() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://lescastcodeurs.com/>"),
            List.of(
                Messages.of(" • note 1\n• \tnote 2\t\n• <https://test.io|test> \n"),
                Messages.of("note 4")));
    var note = new ShowNote(message);

    assertEquals(
        List.of("- note 1", "- note 2\t", "- [test](https://test.io) ", "- note 4"),
        note.comments());
  }

  @Test
  void appAndBotRepliesAreFilteredOut() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://lescastcodeurs.com/>"),
            List.of(
                Messages.of("note 1"),
                Messages.of("app reply", List.of(), "XXX", null),
                Messages.of("bot reply", List.of(), null, "XXX"),
                Messages.of("note 2")));
    var note = new ShowNote(message);

    assertEquals(List.of("- note 1", "- note 2"), note.comments());
  }

  @Test
  void repliesWithMentionsAreFilteredOut() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://lescastcodeurs.com/>"),
            List.of(
                Messages.of("note 1"),
                Messages.of("<@user> mention"),
                Messages.of("mention <!channel>"),
                Messages.of("a <!here> mention"),
                Messages.of("note 2")));
    var note = new ShowNote(message);

    assertEquals(List.of("- note 1", "- note 2"), note.comments());
  }

  @Test
  void repliesWithMentionsAreNotFilteredIfExplicitlyIncluded() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://lescastcodeurs.com/>"),
            List.of(
                Messages.of("note 1"),
                Messages.of("<@user> mention", List.of(INCLUDE.reaction())),
                Messages.of("note 2")));
    var note = new ShowNote(message);

    assertEquals(List.of("- note 1", "- <@user> mention", "- note 2"), note.comments());
  }

  @Test
  void explicitlyExcludedRepliesAreFilteredOut() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://lescastcodeurs.com/>"),
            List.of(
                Messages.of("note 1"),
                Messages.of("excluded 1", List.of(EXCLUDE.reaction())),
                Messages.of("excluded 2", List.of(EXCLUDE.reaction())),
                Messages.of("note 2")));
    var note = new ShowNote(message);

    assertEquals(List.of("- note 1", "- note 2"), note.comments());
  }

  @Test
  void repliesAreProperlyOrderedByDefault() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://test.io>"),
            List.of(
                Messages.of("2", "b", List.of(), null, null),
                Messages.of("1", "a", List.of(), null, null),
                Messages.of("3", "c", List.of(), null, null)));
    var note = new ShowNote(message);

    assertEquals(List.of("- a", "- b", "- c"), note.comments());
  }

  @Test
  void repliesAreProperlyOrderWhenOrderIsForced() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://test.io>"),
            List.of(
                Messages.of("1", "a", List.of("lcc_9"), null, null),
                Messages.of("2", "b", List.of("lcc_5"), null, null),
                Messages.of("3", "c", List.of("lcc_1"), null, null)));
    var note = new ShowNote(message);

    assertEquals(List.of("- c", "- b", "- a"), note.comments());
  }
}
