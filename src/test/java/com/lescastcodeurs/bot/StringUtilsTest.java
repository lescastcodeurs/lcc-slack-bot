package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.StringUtils.asFilename;
import static com.lescastcodeurs.bot.StringUtils.normalize;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

  @Test
  void normalizeWithNullThrowsNullPointer() {
    assertThrows(NullPointerException.class, () -> normalize(null));
  }

  @Test
  void normalizeWithBlank() {
    assertEquals("", normalize(" \t\n"));
  }

  @Test
  void normalizeWithAlphaNumerics() {
    assertEquals("abcdef012", normalize("abcDEF012"));
  }

  @Test
  void normalizeWithAccents() {
    assertEquals("aaeeuci", normalize("àÀéèùçî"));
  }

  @Test
  void normalizeWithUntrimmed() {
    assertEquals("abc", normalize(" abc\t\n"));
  }

  @Test
  void normalizeWithSpecialChars() {
    assertEquals(",| \t\n]=+", normalize(",| \t\n]=+"));
  }

  @Test
  void asFilenameWithNullThrowsNullPointer() {
    assertThrows(NullPointerException.class, () -> asFilename(null, "md"));
    assertThrows(NullPointerException.class, () -> asFilename("test", null));
  }

  @Test
  void asFilenameWithAlphaNumerics() {
    assertEquals("abcdef012.md", asFilename("abcDEF012", "MD"));
  }

  @Test
  void asFilenameWithBlank() {
    assertEquals("-.", asFilename(" \t\n", "\t "));
  }

  @Test
  void asFilenameWithAccents() {
    assertEquals("aaeeuci.md", asFilename("àÀéèùçî", "md"));
  }

  @Test
  void asFilenameWithUntrimmed() {
    assertEquals("abc.md", asFilename(" abc\t\n", " md\n\t"));
  }

  @Test
  void asFilenameWithSpecialChars() {
    assertEquals("-.", asFilename(",| \t\n]=+", "[]"));
  }
}
