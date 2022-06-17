package com.lescastcodeurs.bot;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A wrapper around {@link SlackMessage} to allow customization for show notes generation.
 * <p>
 * This wrapper is not thread safe.
 */
public class ShowNote {

  private static final String URL_REGEX =
    "<(?<url>https?://.+)>( [(](?<category>[a-z]+)[)])?.*";
  private static final Pattern URL_PATTERN = Pattern.compile(
    URL_REGEX,
    CASE_INSENSITIVE
  );

  private final SlackMessage message;
  private final Matcher urlMatcher;

  public static boolean isShowNote(SlackMessage message) {
    return URL_PATTERN.matcher(message.text()).matches();
  }

  public ShowNote(SlackMessage message) {
    this.message = requireNonNull(message);
    this.urlMatcher = URL_PATTERN.matcher(message.text());

    if (!urlMatcher.find()) {
      throw new IllegalArgumentException(
        "message does not look like a show note entry, dit you call isShowNote() to check it ?"
      );
    }
  }

  public String timestamp() {
    return message.timestamp();
  }

  public String text() {
    return message.text();
  }

  public String url() {
    return urlMatcher.group("url");
  }

  public String category() {
    String category = urlMatcher.group("category");
    return category == null ? "unknown" : category;
  }

  public List<String> replies() {
    return message.replies();
  }

  public List<String> comments() {
    List<String> comments = new ArrayList<>();

    for (String reply : message.replies()) {
      Arrays
        .stream(reply.split("[\n•]+"))
        .map(String::trim)
        .filter(not(String::isBlank))
        .forEachOrdered(comments::add);
    }

    return List.copyOf(comments);
  }
}
