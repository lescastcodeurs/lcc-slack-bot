package com.lescastcodeurs.bot;

import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ShowNoteTemplateTest {

  @Location("show-notes.md")
  private Template notes;

  @Test
  void generateEmpty() {
    String rendered = notes.render(new ShowNotes(history()));

    assertNotNull(rendered);
    assertTrue(rendered.startsWith("---"));
    assertTrue(rendered.contains("title:"));
    assertTrue(rendered.contains("author:"));
    assertTrue(rendered.contains("team:"));
    assertTrue(rendered.contains("layout:"));
    assertTrue(rendered.contains("episode:"));
    assertTrue(rendered.contains("mp3_length:"));
    assertTrue(rendered.contains("tweet:"));
    assertTrue(rendered.contains("# tweet size:"));
    assertTrue(rendered.contains("Enregistré le"));
    assertTrue(rendered.contains("Téléchargement de l’épisode"));
    assertTrue(rendered.contains("Soutenez Les Cast Codeurs sur Patreon"));
  }

  @Test
  void generateWithLinks() {
    String rendered = notes.render(
      new ShowNotes(
        history(
          "random comment 1",
          "<https://openjdk.java.net/projects/leyden/notes/01-beginnings>",
          "random comment 2",
          "<https://foojay.io/today/7-reasons-why-after-26-years-java-still-makes-sense/>",
          "@lcc generate show notes"
        )
      )
    );

    assertNotNull(rendered);
    assertTrue(rendered.startsWith("---"));
    assertTrue(
      rendered.contains(
        "(https://openjdk.java.net/projects/leyden/notes/01-beginnings)"
      )
    );
    assertTrue(
      rendered.contains(
        "(https://foojay.io/today/7-reasons-why-after-26-years-java-still-makes-sense/)"
      )
    );
    assertFalse(rendered.contains("random comment"));
    assertFalse(rendered.contains("generate show notes"));
  }

  private List<SlackMessage> history(String... messages) {
    return stream(messages)
      .map(message -> new SlackMessage(null, message, List.of()))
      .toList();
  }
}
