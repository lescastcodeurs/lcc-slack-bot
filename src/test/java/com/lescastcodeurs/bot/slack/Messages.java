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
    return of(timestamp, text, null, null);
  }

  public static Message of(String timestamp, String text, String appId, String botId) {
    Message message = new Message();
    message.setTs(timestamp);
    message.setText(text);
    message.setAppId(appId);
    message.setBotId(botId);
    return message;
  }
}
