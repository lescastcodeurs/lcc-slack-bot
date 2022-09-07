package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.ShowNoteCategory.EXCLUDE;
import static java.util.Objects.requireNonNull;

import com.lescastcodeurs.bot.slack.SlackThread;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** A wrapper around {@link SlackThread} to allow customization for show notes generation. */
public final class ShowNote {

  public static final int DEFAULT_ORDER = 999;
  private final SlackThread thread;

  public ShowNote(SlackThread thread) {
    this.thread = requireNonNull(thread);
  }

  public ShowNoteCategory category() {
    ShowNoteCategory category = null;

    for (String reaction : thread.reactions()) {
      Optional<ShowNoteCategory> guessed = ShowNoteCategory.find(reaction);
      if (guessed.isPresent()) {
        category = guessed.get();
      }
    }

    return category;
  }

  public int order() {
    int order = DEFAULT_ORDER;

    for (String reaction : thread.reactions()) {
      if (reaction.matches("lcc_[1-9]")) {
        order = Integer.parseInt(reaction.split("_")[1]);
      }
    }

    return order;
  }

  public boolean mustBeIncluded() {
    if (thread.isAppMessage()) {
      return false; // application or bot message
    }

    if (thread.reactions().contains(EXCLUDE.reaction())) {
      return false; // Exclude reaction has priority over other reactions (#57).
    }

    ShowNoteCategory category = category();
    if (category == null) {
      if (thread.hasMention()) {
        return false;
      } else {
        return thread.hasLink();
      }
    }

    return true; // Message with a known reaction are always included.
  }

  public String timestamp() {
    return thread.timestamp();
  }

  public String rawText() {
    return thread.text();
  }

  public String text() {
    return thread.asMarkdown();
  }

  public List<String> comments() {
    return thread.replies().stream()
        .map(ShowNoteReply::new)
        .filter(ShowNoteReply::mustBeIncluded)
        .sorted(Comparator.comparing(ShowNoteReply::order).thenComparing(ShowNoteReply::timestamp))
        .flatMap(ShowNoteReply::comments)
        .toList();
  }
}
