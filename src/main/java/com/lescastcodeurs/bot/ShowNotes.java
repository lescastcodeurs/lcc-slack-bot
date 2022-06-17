package com.lescastcodeurs.bot;

import java.time.LocalDateTime;
import java.util.List;

public class ShowNotes {

  public final LocalDateTime now;
  public final List<ShowNote> notes;

  public ShowNotes(List<SlackMessage> messages) {
    this.now = LocalDateTime.now();
    this.notes =
      messages
        .stream()
        .filter(ShowNote::isShowNote)
        .map(ShowNote::new)
        .toList();
  }
}
