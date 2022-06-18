package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.ShowNoteCategory.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ShowNoteCategoryTest {

  @Test
  void acceptNullOrBlank() {
    assertTrue(ShowNoteCategory.find(null).isEmpty());
    assertTrue(ShowNoteCategory.find("").isEmpty());
    assertTrue(ShowNoteCategory.find("\n \t").isEmpty());
  }

  @Test
  void caseInsensitive() {
    assertEquals(CLOUD, ShowNoteCategory.find("cloud").get());
    assertEquals(CLOUD, ShowNoteCategory.find("CLOUD").get());
    assertEquals(CLOUD, ShowNoteCategory.find("ClOuD").get());
  }

  @Test
  void accentInsensitive() {
    assertEquals(SECURITY, ShowNoteCategory.find("securite").get());
    assertEquals(SECURITY, ShowNoteCategory.find("sécurité").get());
    assertEquals(SECURITY, ShowNoteCategory.find("sécurite").get());
  }

  @Test
  void trimInsensitive() {
    assertEquals(CLOUD, ShowNoteCategory.find(" cloud").get());
    assertEquals(CLOUD, ShowNoteCategory.find("cloud ").get());
    assertEquals(CLOUD, ShowNoteCategory.find("\t cloud \n").get());
  }
}
