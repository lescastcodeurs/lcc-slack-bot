package com.lescastcodeurs.bot;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ShowNoteCategoryTest {

  @Test
  void acceptNullOrBlank() {
    assertTrue(ShowNoteCategory.find(null).isEmpty());
    assertTrue(ShowNoteCategory.find("").isEmpty());
    assertTrue(ShowNoteCategory.find("\n \t").isEmpty());
  }
}
