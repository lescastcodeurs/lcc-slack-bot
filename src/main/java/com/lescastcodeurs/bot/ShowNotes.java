package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.ShowNoteCategory.INCLUDE;
import static java.util.Objects.requireNonNull;

import com.lescastcodeurs.bot.slack.SlackThread;
import io.quarkus.qute.TemplateData;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@TemplateData
public class ShowNotes {

  public static final int DEFAULT_EPISODE_NUMBER = 999;
  public static final Pattern EPISODE_NUMBER_PATTERN = Pattern.compile(".*?(?<number>[0-9]+).*?");

  private final LocalDateTime now;
  private final Locale locale;
  private final String title;
  private final Map<ShowNoteCategory, List<ShowNote>> notes;
  private final Conferences conferences;

  public ShowNotes(String title, List<SlackThread> threads, Conferences conferences) {
    this.now = LocalDateTime.now();
    this.locale = Locale.FRANCE;
    this.title = requireNonNull(title);
    this.conferences = conferences;

    this.notes = new EnumMap<>(ShowNoteCategory.class);
    threads.stream()
        .map(ShowNote::new)
        .filter(ShowNote::mustBeIncluded)
        .sorted(Comparator.comparing(ShowNote::order).thenComparing(ShowNote::timestamp))
        .forEach(
            note -> {
              ShowNoteCategory category = note.category();
              category = (category == null ? INCLUDE : category);

              if (!notes.containsKey(category)) {
                notes.put(category, new ArrayList<>());
              }
              notes.get(category).add(note);
            });
  }

  public LocalDateTime now() {
    return now;
  }

  public Locale locale() {
    return locale;
  }

  public Conferences conferences() {
    return conferences;
  }

  public int episodeNumber() {
    Matcher matcher = EPISODE_NUMBER_PATTERN.matcher(title);

    if (matcher.matches()) {
      return Integer.parseInt(matcher.group("number"));
    }

    return DEFAULT_EPISODE_NUMBER;
  }

  public boolean hasNotes(String name) {
    ShowNoteCategory category = ShowNoteCategory.valueOf(name);
    return notes.containsKey(category);
  }

  public List<ShowNote> notes(String name) {
    ShowNoteCategory category = ShowNoteCategory.valueOf(name);

    if (notes.containsKey(category)) {
      return List.copyOf(notes.get(category));
    }

    return List.of();
  }
}
