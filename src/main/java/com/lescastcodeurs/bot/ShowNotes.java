package com.lescastcodeurs.bot;

import java.time.LocalDateTime;
import java.util.List;

public class ShowNotes {

  public final LocalDateTime now;
  public final List<SlackMessage> links;

  public ShowNotes(List<SlackMessage> messages) {
    this.now = LocalDateTime.now();
    this.links =
      messages
        .stream()
        .filter(SlackMessage::isShowNoteEntry)
        .map(SlackMessage::asShowNotesEntry)
        .toList();
  }
}
