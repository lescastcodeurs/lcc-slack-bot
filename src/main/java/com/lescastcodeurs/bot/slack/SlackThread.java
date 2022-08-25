package com.lescastcodeurs.bot.slack;

import com.slack.api.model.Message;
import java.util.List;

public final class SlackThread extends SlackMessage {

  private final List<SlackReply> replies;

  public SlackThread(Message message, List<Message> replies) {
    super(message);
    this.replies = replies == null ? List.of() : replies.stream().map(SlackReply::new).toList();
  }

  public List<SlackReply> replies() {
    return replies;
  }
}
