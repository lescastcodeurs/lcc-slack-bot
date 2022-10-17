package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.ShowNoteCategory.INCLUDE;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.*;

import com.lescastcodeurs.bot.slack.Messages;
import com.lescastcodeurs.bot.slack.SlackThread;
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
    ShowNotes showNotes = new ShowNotes("test", List.of(), new Conferences(null, List.of()), now());
    String rendered = notes.render(showNotes);

    assertEquals(ShowNotes.DEFAULT_EPISODE_NUMBER, showNotes.episodeNumber());
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
  void generateWithNotesAndConferences() {
    List<SlackThread> threads = new ArrayList<>();
    threads.add(thread("random comment 1"));
    for (ShowNoteCategory category : ShowNoteCategory.values()) {
      threads.add(thread("<https://lescastcodeurs.com/" + category + ">", category));
    }
    threads.add(thread("random comment 2"));
    threads.add(thread("@lcc generate show notes"));

    Conferences conferences =
        new Conferences(
            """
      ## 2022

      ### October

      * 6-7: [Paris Web](https://paris-web.fr/) - Paris (France) <img alt="Closed Captions" src="https://img.shields.io/static/v1?label=CC&message=Closed%20Captions&color=blue" /> <a href="https://appel.paris-web.fr/"><img alt="CFP Paris Web" src="https://img.shields.io/static/v1?label=CFP&message=24-Mar-2022-%3E24-Apr-2022&color=red"> </a>
      """,
            List.of("(France)"));

    ShowNotes showNotes = new ShowNotes("test-123", threads, conferences, now());
    String rendered = notes.render(showNotes);

    assertEquals(123, showNotes.episodeNumber());
    assertNotNull(rendered);
    assertTrue(rendered.startsWith("---"));
    assertContains(
        rendered,
        "### Non catégorisées\n\n[https://lescastcodeurs.com/INCLUDE](https://lescastcodeurs.com/INCLUDE)");
    assertContains(
        rendered,
        "### Langages\n\n[https://lescastcodeurs.com/LANGUAGES](https://lescastcodeurs.com/LANGUAGES)");
    assertContains(
        rendered,
        "### Librairies\n\n[https://lescastcodeurs.com/LIBRARIES](https://lescastcodeurs.com/LIBRARIES)");
    assertContains(
        rendered,
        "### Infrastructure\n\n[https://lescastcodeurs.com/INFRASTRUCTURE](https://lescastcodeurs.com/INFRASTRUCTURE)");
    assertContains(
        rendered,
        "### Cloud\n\n[https://lescastcodeurs.com/CLOUD](https://lescastcodeurs.com/CLOUD)");
    assertContains(
        rendered, "### Web\n\n[https://lescastcodeurs.com/WEB](https://lescastcodeurs.com/WEB)");
    assertContains(
        rendered, "### Data\n\n[https://lescastcodeurs.com/DATA](https://lescastcodeurs.com/DATA)");
    assertContains(
        rendered,
        "### Outillage\n\n[https://lescastcodeurs.com/TOOLING](https://lescastcodeurs.com/TOOLING)");
    assertContains(
        rendered,
        "### Architecture\n\n[https://lescastcodeurs.com/ARCHITECTURE](https://lescastcodeurs.com/ARCHITECTURE)");
    assertContains(
        rendered,
        "### Méthodologies\n\n[https://lescastcodeurs.com/METHODOLOGIES](https://lescastcodeurs.com/METHODOLOGIES)");
    assertContains(
        rendered,
        "### Sécurité\n\n[https://lescastcodeurs.com/SECURITY](https://lescastcodeurs.com/SECURITY)");
    assertContains(
        rendered,
        "### Loi, société et organisation\n\n[https://lescastcodeurs.com/SOCIETY](https://lescastcodeurs.com/SOCIETY)");
    assertContains(
        rendered,
        "## Outils de l’épisode\n\n[https://lescastcodeurs.com/TOOL_OF_THE_EPISODE](https://lescastcodeurs.com/TOOL_OF_THE_EPISODE)");
    assertContains(
        rendered,
        "## Rubrique débutant\n\n[https://lescastcodeurs.com/BEGINNERS](https://lescastcodeurs.com/BEGINNERS)");
    assertContains(rendered, "Paris Web");

    assertFalse(rendered.contains("random comment"));
    assertFalse(rendered.contains("generate show notes"));
  }

  @Test
  void whenNoForcedOrderAndCorrectOrder_thenProperlyOrdered() {
    ShowNotes showNotes =
        new ShowNotes(
            "test",
            List.of(
                thread("1", "a", INCLUDE.reaction()),
                thread("2", "b", INCLUDE.reaction()),
                thread("3", "c", INCLUDE.reaction())),
            null,
            now());

    assertEquals(
        List.of("a", "b", "c"),
        showNotes.notes(INCLUDE.name()).stream().map(ShowNote::text).toList());
  }

  @Test
  void whenNoForcedOrderAndIncorrectOrder_thenProperlyOrdered() {
    ShowNotes showNotes =
        new ShowNotes(
            "test",
            List.of(
                thread("2", "b", INCLUDE.reaction()),
                thread("1", "a", INCLUDE.reaction()),
                thread("3", "c", INCLUDE.reaction())),
            null,
            now());

    assertEquals(
        List.of("a", "b", "c"),
        showNotes.notes(INCLUDE.name()).stream().map(ShowNote::text).toList());
  }

  @Test
  void whenForcedOrder_thenProperlyOrdered() {
    ShowNotes showNotes =
        new ShowNotes(
            "test",
            List.of(
                thread("1", "a", INCLUDE.reaction(), "lcc_3"),
                thread("2", "b", INCLUDE.reaction(), "lcc_2"),
                thread("3", "c", INCLUDE.reaction(), "lcc_1")),
            null,
            now());

    assertEquals(
        List.of("c", "b", "a"),
        showNotes.notes(INCLUDE.name()).stream().map(ShowNote::text).toList());
  }

  @Test
  void whenMultipleNumbersInTitle_theFirstOneIsTheEpisodeNumber() {
    ShowNotes showNotes = new ShowNotes("test42 54", List.of(), null, now());

    assertEquals(42, showNotes.episodeNumber());
  }

  private void assertContains(String actual, String expected) {
    assertTrue(actual.contains(expected));
  }

  private SlackThread thread(String message) {
    return new SlackThread(
        Messages.of(message),
        List.of(Messages.of("comment 1"), Messages.of("comment 2"), Messages.of("comment 3")));
  }

  private SlackThread thread(String message, ShowNoteCategory category) {
    return thread(null, message, category.reaction());
  }

  private SlackThread thread(String ts, String message, String... reactions) {
    return new SlackThread(
        Messages.of(ts, message, List.of(reactions), null, null),
        List.of(Messages.of("comment 1"), Messages.of("comment 2"), Messages.of("comment 3")));
  }
}
