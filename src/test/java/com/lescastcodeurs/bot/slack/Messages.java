package com.lescastcodeurs.bot.slack;

import com.slack.api.model.Message;

public final class Messages {

  private Messages() {
    // prevent instantiation
  }

  public static Message of(String text) {
    return of(SlackMessage.DEFAULT_TS, text);
  }

  public static Message of(String timestamp, String text) {
    Message message = new Message();
    message.setTs(timestamp);
    message.setText(text);
    return message;
  }
}
