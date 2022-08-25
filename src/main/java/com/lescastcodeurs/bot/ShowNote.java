package com.lescastcodeurs.bot;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static java.util.regex.Pattern.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A wrapper around {@link SlackMessage} to allow customization for show notes generation.
 *
 * <p>This wrapper is not thread safe.
 */
public class ShowNote {

  // First wildcard is non-greedy : only the first link is considered.
  private static final Pattern SHOW_NOTE_PATTERN =
      Pattern.compile(
          "^.*?(?<note><https?://[^>]+>)\\s*(\\((?<category>[^)]+)\\))?.*$",
          CASE_INSENSITIVE | DOTALL);

  // This patten works against the generated markdown. Must be kept in sync with SHOW_NOTE_PATTERN.
  private static final Pattern CATEGORY_ERASER_PATTERN =
      Pattern.compile(
          "^(?<before>.*?\\(https?://[^)]+\\))(?<category>\\s*\\([^)]+\\))?(?<after>.*)$",
          CASE_INSENSITIVE | DOTALL);

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
    String markdown = message.asMarkdown();
    Optional<ShowNoteCategory> category = ShowNoteCategory.find(urlMatcher.group("category"));

    if (category.isPresent()) {
      Matcher markdownMatcher = CATEGORY_ERASER_PATTERN.matcher(markdown);
      if (markdownMatcher.matches()) {
        return markdownMatcher.replaceFirst("${before}${after}");
      }
    }

    return markdown;
  }

  public ShowNoteCategory category() {
    String category = urlMatcher.group("category");
    return ShowNoteCategory.find(category).orElse(ShowNoteCategory.NEWS);
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
