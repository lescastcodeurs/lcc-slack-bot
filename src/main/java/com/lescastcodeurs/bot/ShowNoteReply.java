package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.ShowNoteCategory.EXCLUDE;
import static com.lescastcodeurs.bot.ShowNoteCategory.INCLUDE;
import static java.util.function.Predicate.not;

import com.lescastcodeurs.bot.slack.SlackReply;
import java.util.Objects;
import java.util.stream.Stream;

public final class ShowNoteReply {

  public static final int DEFAULT_ORDER = 999;

  private final SlackReply reply;

  public ShowNoteReply(SlackReply reply) {
    this.reply = Objects.requireNonNull(reply);
  }

  public int order() {
    int order = DEFAULT_ORDER;

    for (String reaction : reply.reactions()) {
      if (reaction.matches("lcc_[1-9]")) {
        order = Integer.parseInt(reaction.split("_")[1]);
      }
    }

    return order;
  }

  public boolean mustBeIncluded() {
    if (reply.isAppMessage()) {
      return false;
    } else if (reply.reactions().contains(EXCLUDE.reaction())) {
      return false; // Exclude reaction has priority over other reactions (#57).
    } else if (reply.reactions().contains(INCLUDE.reaction())) {
      return true;
    }

    return !reply.hasMention();
  }

  public String timestamp() {
    return reply.timestamp();
  }

  public Stream<String> comments() {
    return reply
        .asMarkdown()
        .lines()
        .filter(not(String::isBlank))
        .map(line -> (!line.startsWith("-") && !line.startsWith("  -")) ? "- " + line : line);
  }
}
