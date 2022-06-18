package com.lescastcodeurs.bot;

public interface TestConstants {
  String TS = "0123456789.123456";

  ShowNoteCategory CATEGORY = ShowNoteCategory.CLOUD;
  String URL = "https://google.com";
  String HTTP_URL = "http://google.com";

  String NOTE_URL = "<%s> (%s)".formatted(URL, CATEGORY);
  String NOTE_HTTP_URL = "<%s> (%s)".formatted(HTTP_URL, CATEGORY);
  String UNCATEGORIZED_NOTE_URL = "<%s>".formatted(URL);
}
