package com.lescastcodeurs.bot.slack;

import com.slack.api.model.Message;

public final class SlackReply extends SlackMessage {
  public SlackReply(Message message) {
    super(message);
  }
}
