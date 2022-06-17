package com.lescastcodeurs.bot;

import static java.util.Arrays.stream;
import static java.util.function.Predicate.not;

import com.slack.api.model.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract required information from a Slack message.
 */
public final class SlackMessage {

  private static final String SHOW_NOTE_REGEX = "<(https?://.+)>.*";
  private static final Pattern SHOW_NOTE_PATTERN = Pattern.compile(SHOW_NOTE_REGEX, Pattern.CASE_INSENSITIVE);

  // See https://github.com/slackhq/slack-api-docs/issues/7#issuecomment-67913241.
  private final String timestamp;
  private final String text;
  private final List<String> replies;

  public SlackMessage(String timestamp, String text, List<String> replies) {
    this.timestamp = timestamp == null ? "9999999999.999999" : timestamp;
    this.text = text == null ? "" : text;
    this.replies = replies == null ? List.of() : List.copyOf(replies);
  }

  public SlackMessage(Message message, List<String> replies) {
    this(message.getTs(), message.getText(), replies);
  }

  public String timestamp() {
    return timestamp;
  }

  public String text() {
    return text;
  }

  public List<String> replies() {
    return replies;
  }

  public boolean isShowNoteEntry() {
    return SHOW_NOTE_PATTERN.matcher(text).matches();
  }

  /**
   * Transform this message to an LCC show notes entry.
   */
  public SlackMessage asShowNotesEntry() {
    Matcher matcher = SHOW_NOTE_PATTERN.matcher(text);
    if (!matcher.find( )) {
      throw new UnsupportedOperationException("this message does not look like a show note entry, dit you call isShowNoteEntry() to check it ?");
    }

    String url = matcher.group(1);
    List<String> newReplies = new ArrayList<>();
    for (String reply : replies) {
      stream(reply.split("[\n•]+"))
        .map(String::trim)
        .filter(not(String::isBlank))
        .forEachOrdered(newReplies::add);
    }

    return new SlackMessage(timestamp, url, newReplies);
  }
}
