package com.lescastcodeurs.bot;

import com.slack.api.model.Message;

/**
 * Extract required information from a Slack message.
 */
public final class SlackMessage {

  // See https://github.com/slackhq/slack-api-docs/issues/7#issuecomment-67913241.
  private final String timestamp;
  private final String text;

  public SlackMessage(String timestamp, String text) {
    this.timestamp = timestamp == null ? "9999999999.999999" : timestamp;
    this.text = text == null ? "" : text;
  }

  public SlackMessage(Message message) {
    this(message.getTs(), message.getText());
  }

  public String timestamp() {
    return timestamp;
  }

  public String text() {
    return text;
  }

  public boolean isLink() {
    return text.startsWith("<http") && text.endsWith(">");
  }

  public String url() {
    return text.substring(1).substring(0, text.length() - 2);
  }
}
