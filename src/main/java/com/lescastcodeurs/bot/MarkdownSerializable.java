package com.lescastcodeurs.bot;

/** An object that can be converted to markdown. */
public interface MarkdownSerializable {

  /**
   * Convert this instance to markdown.
   *
   * @return a non-null {@link String}
   */
  String markdown();
}
