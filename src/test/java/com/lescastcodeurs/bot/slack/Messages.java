package com.lescastcodeurs.bot.slack;

import com.slack.api.model.Message;
import com.slack.api.model.Reaction;
import java.util.List;

public final class Messages {

  private Messages() {
    // prevent instantiation
  }

  public static Message of(String text) {
    return of(text, null);
  }

  public static Message of(String text, List<String> reactions) {
    return of(text, reactions, null, null);
  }

  public static Message of(String text, List<String> reactions, String appId, String botId) {
    return of(SlackMessage.DEFAULT_TS, text, reactions, appId, botId);
  }

  public static Message of(
      String ts, String text, List<String> reactions, String appId, String botId) {
    Message message = new Message();
    message.setTs(ts);
    message.setText(text);
    message.setAppId(appId);
    message.setBotId(botId);

    if (reactions != null) {
      message.setReactions(
          reactions.stream()
              .map(
                  name -> {
                    Reaction reaction = new Reaction();
                    reaction.setName(name);
                    reaction.setCount(1);
                    reaction.setUsers(List.of("XXX"));
                    return reaction;
                  })
              .toList());
    }

    return message;
  }
}
