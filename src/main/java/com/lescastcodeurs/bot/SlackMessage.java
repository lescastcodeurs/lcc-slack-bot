package com.lescastcodeurs.bot;

import static java.util.Arrays.stream;
import static java.util.function.Predicate.not;

import com.slack.api.model.Message;
import java.util.ArrayList;
import java.util.List;

/**
 * Extract required information from a Slack message.
 */
public final class SlackMessage {

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
    return text.startsWith("<http") && text.endsWith(">");
  }

  /**
   * Transform this message to an LCC show notes entry.
   */
  public SlackMessage asShowNotesEntry() {
    String url = text.length() > 3
      ? text.substring(1).substring(0, text.length() - 2)
      : text;

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
