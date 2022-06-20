package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.ShowNote.isShowNote;
import static com.lescastcodeurs.bot.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class ShowNoteTest {

  @Test
  void isShowNoteEntry() {
    assertTrue(isShowNote(new SlackMessage(TS, NOTE_URL, List.of())));
    assertTrue(isShowNote(new SlackMessage(TS, NOTE_HTTP_URL, List.of())));
    assertTrue(
      isShowNote(new SlackMessage(TS, UNCATEGORIZED_NOTE_URL, List.of()))
    );

    assertFalse(isShowNote(new SlackMessage(TS, URL, List.of())));
    assertFalse(isShowNote(new SlackMessage(TS, "whatever", List.of())));
  }

  @Test
  void messageCannotBeNull() {
    assertThrows(NullPointerException.class, () -> new ShowNote(null));
  }

  @Test
  void url() {
    var message = new SlackMessage(TS, NOTE_URL, List.of());
    var note = new ShowNote(message);

    assertTrue(note.text().contains(URL));
  }

  @Test
  void category() {
    var message = new SlackMessage(TS, NOTE_URL, List.of());
    var note = new ShowNote(message);

    assertEquals(CATEGORY, note.category());
  }

  @Test
  void comments() {
    SlackMessage message = new SlackMessage(
      TS,
      NOTE_URL,
      List.of(" • note 1\n• \tnote 2\t\n• note 3 \n")
    );
    var note = new ShowNote(message);

    assertEquals(
      List.of("- note 1", "- note 2\t", "- note 3 "),
      note.comments()
    );
  }
}
