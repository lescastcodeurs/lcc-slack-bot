package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.ShowNoteCategory.EXCLUDE;
import static com.lescastcodeurs.bot.ShowNoteCategory.INCLUDE;
import static java.util.function.Predicate.not;

import com.lescastcodeurs.bot.slack.SlackReply;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class ShowNoteReply {

  public static final int DEFAULT_ORDER = 999;

  private static final Pattern LIST_ITEM_PATTERN = Pattern.compile("^ *-");

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
    String markdown = reply.asMarkdown();
    boolean isPlainList = markdown.startsWith("-");

    return markdown
        .lines()
        .filter(not(String::isBlank))
        .map(
            line -> {
              if (LIST_ITEM_PATTERN.matcher(line).find()) {
                // Lists that do not belong to plain lists messages have to be shifted. See
                // corresponding tests.
                return isPlainList ? line : "  " + line;
              } else {
                return "- " + line;
              }
            });
  }
}
