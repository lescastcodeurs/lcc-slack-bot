package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.SlackMessage.DEFAULT_TS;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.test.junit.QuarkusTest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ShowNotesTest {

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
    assertTrue(rendered.contains(Integer.toString(LocalDate.now().getYear())));
    assertTrue(rendered.contains("Téléchargement de l’épisode"));
    assertTrue(rendered.contains("Soutenez Les Cast Codeurs sur Patreon"));
    assertTrue(rendered.endsWith("<!-- vim: set spelllang=fr : -->\n"));
  }

  @Test
  void generateWithNotes() {
    List<String> messages = new ArrayList<>();
    messages.add("random comment 1");
    for (ShowNoteCategory category : ShowNoteCategory.values()) {
      String label = category.getLabels().stream().findFirst().orElseThrow();
      String url = "<https://lescastcodeurs.com/" + category + "> (" + label + ")";
      messages.add(url);
    }
    messages.add("random comment 2");
    messages.add("@lcc generate show notes");

    String rendered = notes.render(new ShowNotes(history(messages)));

    assertNotNull(rendered);
    assertTrue(rendered.startsWith("---"));
    for (ShowNoteCategory category : ShowNoteCategory.values()) {
      String url = "https://lescastcodeurs.com/" + category;
      assertTrue(rendered.contains(url));
    }
    assertTrue(rendered.contains("- comment 1"));
    assertTrue(rendered.contains("- comment 2"));
    assertTrue(rendered.contains("- comment 3"));
    assertTrue(rendered.endsWith("<!-- vim: set spelllang=fr : -->\n"));

    assertFalse(rendered.contains("random comment"));
    assertFalse(rendered.contains("generate show notes"));
  }

  private List<SlackMessage> history(String... messages) {
    return history(asList(messages));
  }

  private List<SlackMessage> history(List<String> messages) {
    return messages.stream()
        .map(
            message ->
                new SlackMessage(
                    DEFAULT_TS, message, List.of("comment 1", "comment 2", "comment 3"), false))
        .toList();
  }
}
