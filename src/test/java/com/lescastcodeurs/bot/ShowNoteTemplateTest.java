package com.lescastcodeurs.bot;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ShowNoteTemplateTest {

  @Location("show-notes.md")
  private Template notes;

  @Test
  void generateEmpty() {
    String rendered = notes.render(new ShowNotes());

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
}
