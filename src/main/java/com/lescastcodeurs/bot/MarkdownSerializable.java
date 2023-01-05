package com.lescastcodeurs.bot;

import java.util.Locale;

/** An object that can be converted to markdown. */
public interface MarkdownSerializable {

  /**
   * Convert this instance to markdown.
   *
   * @return a non-null {@link String}
   */
  String markdown(Locale locale);
}
