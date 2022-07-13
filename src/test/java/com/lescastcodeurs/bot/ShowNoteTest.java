package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.ShowNote.isShowNote;
import static com.lescastcodeurs.bot.ShowNoteCategory.CLOUD;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ShowNoteTest {

  private static final String TS = "0123456789.123456";

  @ParameterizedTest
  @MethodSource("showNoteArgs")
  void isShowNoteEntry(String input, boolean expected) {
    assertEquals(expected, isShowNote(new SlackMessage(TS, input, List.of())));
  }

  private static Stream<Arguments> showNoteArgs() {
    return Stream.of(
      Arguments.of("<https://lescastcodeurs.com/>", true),
      Arguments.of("<https://lescastcodeurs.com/2022/07/12/lcc-281-aperikube-apomorphique-partie-1/>", true),
      Arguments.of("<https://www.lilian-benoit.fr/2022/06/JEP-405-Record-Patterns.html>", true),
      Arguments.of("<https://blog.jetbrains.com/idea/2022/06/intellij-idea-2022-2-eap-5/#Frameworks_and_Technologies>", true),
      Arguments.of("<https://dzone.com/articles/introducing-bolt-neo4js-upcoming-binary-protocol-p?utm_content=bufferda4f9&utm_medium=social&utm_source=twitter.com&utm_campaign=buffer>", true),
      Arguments.of("<https://angular.io/docs/ts/latest/guide/upgrade.html#!#sts=Migrating%20to%20TypeScript>", true),
      Arguments.of("<http://lescastcodeurs.com/>", true),
      Arguments.of("<https://lescastcodeurs.com/|title>", true),
      Arguments.of("news: <https://lescastcodeurs.com/>", true),
      Arguments.of("news: <https://lescastcodeurs.com/|title>", true),
      Arguments.of("news: <https://lescastcodeurs.com/> additional text", true),
      Arguments.of("https://lescastcodeurs.com/", false),
      Arguments.of("@lcc generate show notes", false),
      Arguments.of("<tricky> message", false)
    );
  }

  @Test
  void messageCannotBeNull() {
    assertThrows(NullPointerException.class, () -> new ShowNote(null));
  }

  @Test
  void url() {
    var message = new SlackMessage(TS, "cloud: <https://lescastcodeurs.com/>", List.of());
    var note = new ShowNote(message);

    assertEquals("[https://lescastcodeurs.com/](https://lescastcodeurs.com/)", note.text());
  }

  @Test
  void category() {
    var message = new SlackMessage(TS, "cloud: <https://lescastcodeurs.com/>", List.of());
    var note = new ShowNote(message);

    assertEquals(CLOUD, note.category());
  }

  @Test
  void comments() {
    SlackMessage message =
      new SlackMessage(TS, "<https://lescastcodeurs.com/>", List.of(" • note 1\n• \tnote 2\t\n• note 3 \n"));
    var note = new ShowNote(message);

    assertEquals(List.of("- note 1", "- note 2\t", "- note 3 "), note.comments());
  }
}
