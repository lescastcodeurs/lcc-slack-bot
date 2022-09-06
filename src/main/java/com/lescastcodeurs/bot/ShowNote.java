package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.ShowNoteCategory.EXCLUDE;
import static com.lescastcodeurs.bot.ShowNoteCategory.INCLUDE;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;

import com.lescastcodeurs.bot.slack.SlackReply;
import com.lescastcodeurs.bot.slack.SlackThread;
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

  public boolean isShowNote() {
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

  public String text() {
    return thread.asMarkdown();
  }

  public List<String> comments() {
    return thread.replies().stream()
        .filter(
            reply -> {
              if (reply.isAppMessage()) {
                return false;
              } else if (reply.reactions().contains(EXCLUDE.reaction())) {
                return false; // Exclude reaction has priority over other reactions (#57).
              } else if (reply.reactions().contains(INCLUDE.reaction())) {
                return true;
              } else {
                return !reply.hasMention();
              }
            })
        .map(SlackReply::asMarkdown)
        .flatMap(String::lines)
        .filter(not(String::isBlank))
        .map(line -> (!line.startsWith("-") && !line.startsWith("  -")) ? "- " + line : line)
        .toList();
  }
}
