package com.lescastcodeurs.bot.slack;

import static java.util.Objects.requireNonNull;

import com.slack.api.model.event.AppMentionEvent;

public record SlackMentionEvent(String channel, String threadTs, String ts, String text) {
  public SlackMentionEvent {
    requireNonNull(channel);
    requireNonNull(ts);
    requireNonNull(text);
  }

  public SlackMentionEvent(AppMentionEvent event) {
    this(event.getChannel(), event.getThreadTs(), event.getTs(), event.getText());
  }

  public String replyTs() {
    return threadTs == null ? ts : threadTs;
  }
}
