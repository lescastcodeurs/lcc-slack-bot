package com.lescastcodeurs.bot;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.DOTALL;

import com.lescastcodeurs.bot.slack.SlackReply;
import com.lescastcodeurs.bot.slack.SlackThread;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A wrapper around {@link SlackThread} to allow customization for show notes generation.
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

  private final SlackThread thread;
  private final Matcher urlMatcher;

  public static boolean isShowNote(SlackThread thread) {
    return SHOW_NOTE_PATTERN.matcher(thread.text()).matches();
  }

  public ShowNote(SlackThread thread) {
    this.thread = requireNonNull(thread);
    this.urlMatcher = SHOW_NOTE_PATTERN.matcher(thread.text());

    if (!urlMatcher.find()) {
      throw new IllegalArgumentException(
          "message does not look like a show note entry, dit you call isShowNote() to check it ?");
    }
  }

  public String timestamp() {
    return thread.timestamp();
  }

  public String text() {
    String markdown = thread.asMarkdown();
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
    return thread.replies().stream()
        .map(SlackReply::asMarkdown)
        .flatMap(String::lines)
        .filter(not(String::isBlank))
        .map(this::asListItem)
        .toList();
  }

  private String asListItem(String line) {
    if (!line.startsWith("-") && !line.startsWith("  -")) {
      return "- " + line;
    }

    return line;
  }
}
