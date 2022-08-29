package com.lescastcodeurs.bot;

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
  void appMessageIsNotShowNote() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://lescastcodeurs.com/>", null, "ABCD", null), List.of());
    var note = new ShowNote(message);

    assertFalse(note.isShowNote());
  }

  @Test
  void botMessageIsNotShowNote() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://lescastcodeurs.com/>", null, null, "ABCD"), List.of());
    var note = new ShowNote(message);

    assertFalse(note.isShowNote());
  }

  @Test
  void messageWithoutLinkAndWithoutCategoryIsNotShowNote() {
    SlackThread message = new SlackThread(Messages.of("test", null), List.of());
    var note = new ShowNote(message);

    assertNull(note.category());
    assertFalse(note.isShowNote());
  }

  @Test
  void messageWithoutLinkAndWithCategoryIsShowNote() {
    SlackThread message =
        new SlackThread(Messages.of("test", List.of(CLOUD.reaction())), List.of());
    var note = new ShowNote(message);

    assertEquals(CLOUD, note.category());
    assertTrue(note.isShowNote());
    assertEquals("test", note.text());
  }

  @Test
  void messageWithLinkAndWithoutCategoryIsShowNote() {
    SlackThread message = new SlackThread(Messages.of("<https://test.io>"), List.of());
    var note = new ShowNote(message);

    assertNull(note.category());
    assertTrue(note.isShowNote());
    assertEquals("[https://test.io](https://test.io)", note.text());
  }

  @Test
  void messageWithLinkAndWithUserMentionAndWithoutCategoryIsNotShowNote() {
    SlackThread message = new SlackThread(Messages.of("<http://test.io/> <@XXX>"), List.of());
    var note = new ShowNote(message);

    assertNull(note.category());
    assertFalse(note.isShowNote());
  }

  @Test
  void messageWithLinkAndWithChannelMentionAndWithoutCategoryIsNotShowNote() {
    SlackThread message = new SlackThread(Messages.of("<http://test.io/> <!channel>"), List.of());
    var note = new ShowNote(message);

    assertNull(note.category());
    assertFalse(note.isShowNote());
  }

  @Test
  void messageWithLinkAndWithHereMentionAndWithoutCategoryIsNotShowNote() {
    SlackThread message = new SlackThread(Messages.of("<http://test.io/> <!here>"), List.of());
    var note = new ShowNote(message);

    assertNull(note.category());
    assertFalse(note.isShowNote());
  }

  @Test
  void messageWithLinkAndWithCategoryIsShowNote() {
    SlackThread message =
        new SlackThread(Messages.of("<https://test.io>", List.of(CLOUD.reaction())), List.of());
    var note = new ShowNote(message);

    assertEquals(CLOUD, note.category());
    assertTrue(note.isShowNote());
    assertEquals("[https://test.io](https://test.io)", note.text());
  }

  @Test
  void messageWithLinkAndExplicitlyIgnoredIsNotShowNote() {
    SlackThread message =
        new SlackThread(Messages.of("<https://test.io>", List.of(EXCLUDE.reaction())), List.of());
    var note = new ShowNote(message);

    assertEquals(EXCLUDE, note.category());
    assertFalse(note.isShowNote());
  }

  @Test
  void lastReactionWins1() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://test.io>", List.of(EXCLUDE.reaction(), CLOUD.reaction())),
            List.of());
    var note = new ShowNote(message);

    assertEquals(CLOUD, note.category());
    assertTrue(note.isShowNote());
    assertEquals("[https://test.io](https://test.io)", note.text());
  }

  @Test
  void lastReactionWins2() {
    SlackThread message =
        new SlackThread(
            Messages.of("<https://test.io>", List.of(CLOUD.reaction(), EXCLUDE.reaction())),
            List.of());
    var note = new ShowNote(message);

    assertEquals(EXCLUDE, note.category());
    assertFalse(note.isShowNote());
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
}
