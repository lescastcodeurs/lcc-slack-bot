package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.ShowNote.isShowNote;
import static com.lescastcodeurs.bot.ShowNoteCategory.*;
import static com.lescastcodeurs.bot.SlackMessage.DEFAULT_TS;
import static org.junit.jupiter.api.Assertions.*;

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
  @MethodSource("linkArguments")
  void link(String text, String expectedMarkdown, ShowNoteCategory expectedCategory) {
    SlackMessage message = new SlackMessage(DEFAULT_TS, text, List.of());
    assertTrue(isShowNote(message));

    ShowNote note = new ShowNote(message);
    assertEquals(expectedMarkdown, note.text());
    assertEquals(expectedCategory, note.category());
  }

  private static Stream<Arguments> linkArguments() {
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
            "[https://test.io/](https://test.io/) this is a test",
            NEWS),
        Arguments.of(
            "bla bla\n<https://test.io/d/a.txt?a=b&c=d#!#e=f%20g|this is a test> (cloud)\nbla bla <https://link.to|link> (test)",
            "bla bla [this is a test](https://test.io/d/a.txt?a=b&c=d#!#e=f%20g) bla bla [link](https://link.to) (test)",
            CLOUD));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"<tricky> message", "@lcc generate show notes", "https://lescastcodeurs.com/"})
  void notALink() {
    SlackMessage message = new SlackMessage(DEFAULT_TS, "<tricky> message", List.of());
    assertFalse(isShowNote(message));
  }

  @Test
  void comments() {
    SlackMessage message =
        new SlackMessage(
            DEFAULT_TS,
            "<https://lescastcodeurs.com/>",
            List.of(" • note 1\n• \tnote 2\t\n• <https://test.io|test> \n", "note 4"));
    var note = new ShowNote(message);

    assertEquals(
        List.of("- note 1", "- note 2\t", "- [test](https://test.io) ", "- note 4"),
        note.comments());
  }
}
