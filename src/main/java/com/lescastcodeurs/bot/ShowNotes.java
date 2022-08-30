package com.lescastcodeurs.bot;

import static java.util.Objects.requireNonNull;

import com.lescastcodeurs.bot.slack.SlackThread;
import io.quarkus.qute.TemplateData;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@TemplateData
public class ShowNotes {

  public static final int DEFAULT_EPISODE_NUMBER = 999;
  public static final Pattern EPISODE_NUMBER_PATTERN = Pattern.compile(".*?(?<number>[0-9]+).*?");

  private final LocalDateTime now;
  private final Locale locale;
  private final String title;
  private final List<ShowNote> notes;

  public ShowNotes(String title, List<SlackThread> threads) {
    this.now = LocalDateTime.now();
    this.locale = Locale.FRANCE;
    this.title = requireNonNull(title);
    this.notes = threads.stream().map(ShowNote::new).toList();
  }

  public LocalDateTime now() {
    return now;
  }

  public Locale locale() {
    return locale;
  }

  public int episodeNumber() {
    Matcher matcher = EPISODE_NUMBER_PATTERN.matcher(title);

    if (matcher.matches()) {
      return Integer.parseInt(matcher.group("number"));
    }

    return DEFAULT_EPISODE_NUMBER;
  }

  public List<ShowNote> notes(String name) {
    ShowNoteCategory category = ShowNoteCategory.valueOf(name);
    return notes.stream()
        .filter(
            n ->
                n.isShowNote()
                    && (n.category() == category
                        || (n.category() == null && category == ShowNoteCategory.INCLUDE)))
        .toList();
  }
}
