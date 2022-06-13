package com.lescastcodeurs.bot;

import static java.util.Comparator.*;

import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.model.Message;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ShowNotes {

  public final LocalDateTime now;
  public final List<Link> links;

  public ShowNotes(ConversationsHistoryResponse history) {
    this.now = LocalDateTime.now();
    this.links =
      history
        .getMessages()
        .stream()
        .sorted(comparing(Message::getTs, nullsLast(naturalOrder())))
        .map(ShowNotes::toLink)
        .flatMap(Optional::stream)
        .toList();
  }

  private static Optional<Link> toLink(Message message) {
    String text = message.getText();

    if (text.startsWith("<http") && text.endsWith(">")) {
      String url = text.substring(1).substring(0, text.length() - 2);
      return Optional.of(new Link(url));
    }

    return Optional.empty();
  }

  public static final class Link {

    public final String url;

    public Link(String url) {
      this.url = url;
    }
  }
}
