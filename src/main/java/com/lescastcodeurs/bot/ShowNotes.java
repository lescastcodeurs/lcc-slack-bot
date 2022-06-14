package com.lescastcodeurs.bot;

import static java.util.Comparator.*;

import java.time.LocalDateTime;
import java.util.List;

public class ShowNotes {

  public final LocalDateTime now;
  public final List<SlackMessage> links;

  public ShowNotes(List<SlackMessage> history) {
    this.now = LocalDateTime.now();
    this.links =
      history
        .stream()
        .sorted(comparing(SlackMessage::timestamp))
        .filter(SlackMessage::isLink)
        .toList();
  }
}
