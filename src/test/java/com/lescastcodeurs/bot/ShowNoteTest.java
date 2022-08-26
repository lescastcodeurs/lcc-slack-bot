package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.ShowNoteCategory.*;
import static org.junit.jupiter.api.Assertions.*;

import com.lescastcodeurs.bot.slack.Messages;
import com.lescastcodeurs.bot.slack.SlackThread;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class ShowNoteTest {

  @Test
  void messageCannotBeNull() {
    assertThrows(NullPointerException.class, () -> new ShowNote(null));
  }

  @ParameterizedTest
  @MethodSource("validShowNotes")
  void validShowNotes(String text, String expectedMarkdown, ShowNoteCategory expectedCategory) {
    ShowNote note = new ShowNote(new SlackThread(Messages.of(text), null));

    assertTrue(note.isShowNote());
    assertEquals(expectedMarkdown, note.text());
    assertEquals(expectedCategory, note.category());
  }

  private static Stream<Arguments> validShowNotes() {
    return Stream.of(
        Arguments.of("<https://test.io>", "[https://test.io](https://test.io)", NEWS),
        Arguments.of("<http://test.io>", "[http://test.io](http://test.io)", NEWS),
        Arguments.of(
            "<https://test.io/2022/07/12/new/>",
            "[https://test.io/2022/07/12/new/](https://test.io/2022/07/12/new/)",
            NEWS),
        Arguments.of(
            "<https://test.io/new.html>",
            "[https://test.io/new.html](https://test.io/new.html)",
            NEWS),
        Arguments.of(
            "<https://test.io/?q=test&a=test>",
            "[https://test.io/?q=test&a=test](https://test.io/?q=test&a=test)",
            NEWS),
        Arguments.of(
            "<https://test.io/#fragment>",
            "[https://test.io/#fragment](https://test.io/#fragment)",
            NEWS),
        Arguments.of(
            "<https://test.io/some%20test/>",
            "[https://test.io/some%20test/](https://test.io/some%20test/)", NEWS),
        Arguments.of(
            "<https://test.io/|this is a test>", "[this is a test](https://test.io/)", NEWS),
        Arguments.of("<https://test.io/> (cloud)", "[https://test.io/](https://test.io/)", CLOUD),
        Arguments.of(
            "<https://test.io/> (unknown)", "[https://test.io/](https://test.io/) (unknown)", NEWS),
        Arguments.of(
            "this is a test : <https://test.io/>",
            "this is a test : [https://test.io/](https://test.io/)",
            NEWS),
        Arguments.of(
            "<https://test.io/> : this is a test",
            "[https://test.io/](https://test.io/) : this is a test",
            NEWS),
        Arguments.of(
            "<https://test1.io|test1> et <https://test2.io|test2>",
            "[test1](https://test1.io) et [test2](https://test2.io)",
            NEWS),
        Arguments.of(
            "<https://test1.io|test1> et <https://test2.io|test2> (cloud)",
            "[test1](https://test1.io) et [test2](https://test2.io) (cloud)",
            NEWS),
        Arguments.of(
            "<https://test1.io|test1> (lib) et <https://test2.io|test2> (cloud)",
            "[test1](https://test1.io) et [test2](https://test2.io) (cloud)",
            LIBRARIES),
        Arguments.of(
            "<https://test.io/>\nthis\nis\na\ntest",
            "[https://test.io/](https://test.io/)\nthis\nis\na\ntest",
            NEWS),
        Arguments.of(
            "bla bla \n<https://test.io/d/a.txt?a=b&c=d#!#e=f%20g|this is a test> (cloud) \nbla bla <https://link.to|link> (test)",
            "bla bla \n[this is a test](https://test.io/d/a.txt?a=b&c=d#!#e=f%20g) \nbla bla [link](https://link.to) (test)",
            CLOUD));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "",
        "message without link",
        "message with plain link : https://lescastcodeurs.com/",
        "message with something that is not a <link>",
        "message with <@user> mention",
        "message with <!channel> mention",
        "message with <!here> mention"
      })
  void invalidShowNotes(String message) {
    SlackThread thread = new SlackThread(Messages.of(message), List.of());
    ShowNote note = new ShowNote(thread);

    assertFalse(note.isShowNote());
  }

  @Test
  void appMessageAreInvalid() {
    SlackThread message =
        new SlackThread(
            Messages.of(null, "<https://lescastcodeurs.com/>", "ABCD", null), List.of());
    var note = new ShowNote(message);

    assertFalse(note.isShowNote());
  }

  @Test
  void botMessageAreInvalid() {
    SlackThread message =
        new SlackThread(
            Messages.of(null, "<https://lescastcodeurs.com/>", null, "ABCD"), List.of());
    var note = new ShowNote(message);

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
}
