package com.lescastcodeurs.bot;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A wrapper around {@link SlackMessage} to allow customization for show notes generation.
 *
 * <p>This wrapper is not thread safe.
 */
public class ShowNote {

  private static final Pattern SHOW_NOTE_PATTERN =
      Pattern.compile("^((?<category>[a-z]+):)?\\s*(?<note><https?://.+)$", CASE_INSENSITIVE);

  private static final Pattern CATEGORY_PATTERN =
      Pattern.compile("^((?<category>[a-z]+):\\s*)?", CASE_INSENSITIVE);

  private final SlackMessage message;
  private final Matcher urlMatcher;

  public static boolean isShowNote(SlackMessage message) {
    return SHOW_NOTE_PATTERN.matcher(message.text()).matches();
  }

  public ShowNote(SlackMessage message) {
    this.message = requireNonNull(message);
    this.urlMatcher = SHOW_NOTE_PATTERN.matcher(message.text());

    if (!urlMatcher.find()) {
      throw new IllegalArgumentException(
          "message does not look like a show note entry, dit you call isShowNote() to check it ?");
    }
  }

  public String timestamp() {
    return message.timestamp();
  }

  public String text() {
    if (category() != ShowNoteCategory.NEWS) {
      return CATEGORY_PATTERN.matcher(message.asMarkdown()).replaceAll(""); // strip category
    }

    return message.asMarkdown();
  }

  public ShowNoteCategory category() {
    String category = urlMatcher.group("category");
    return ShowNoteCategory.find(category).orElse(ShowNoteCategory.NEWS);
  }

  public List<String> replies() {
    return message.repliesAsMarkdown();
  }

  public List<String> comments() {
    List<String> comments = new ArrayList<>();

    for (String reply : message.repliesAsMarkdown()) {
      reply
          .lines()
          .filter(not(String::isBlank))
          .map(this::asListItem)
          .forEachOrdered(comments::add);
    }

    return List.copyOf(comments);
  }

  private String asListItem(String line) {
    if (!line.startsWith("-") && !line.startsWith("  -")) {
      return "- " + line;
    }

    return line;
  }
}
