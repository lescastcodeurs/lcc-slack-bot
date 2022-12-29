package com.lescastcodeurs.bot.conferences;

import static com.lescastcodeurs.bot.internal.StringUtils.isNotBlank;

import com.lescastcodeurs.bot.MarkdownSerializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuppressWarnings("java:S6218") // don't care
public record Conference(String name, String hyperlink, String location, String misc, int[] date)
    implements MarkdownSerializable {

  private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("d MMMM uuuu");

  public boolean isValidCandidate(List<String> selectionCriteria, LocalDate date) {
    boolean result = hasValidData();
    result = result && isAfter(date);
    result = result && matchesAnyOf(selectionCriteria);
    return result;
  }

  public boolean hasValidData() {
    return date != null
        && date.length == 2
        && isNotBlank(name)
        && isNotBlank(hyperlink)
        && isNotBlank(location);
  }

  public boolean isAfter(LocalDate d) {
    long end = date[1];
    long now = d.toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC);
    return end >= now;
  }

  public boolean matchesAnyOf(List<String> criteria) {
    for (String criterion : criteria) {
      if (name.contains(criterion) || location.contains(criterion)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public String markdown() {
    LocalDate start = timestampToDate(date[0]);
    LocalDate end = timestampToDate(date[1]);

    String date;
    if (start.equals(end)) {
      date = end.format(FORMAT);
    } else if (start.getMonth() == end.getMonth()) {
      date = start.getDayOfMonth() + "-" + end.format(FORMAT);
    } else {
      date = start.format(FORMAT) + "-" + end.format(FORMAT);
    }

    return "- %s : [%s](%s) - %s %s%n".formatted(date, name, hyperlink, location, misc);
  }

  private static LocalDate timestampToDate(int timestamp) {
    Instant instant = Instant.ofEpochSecond(timestamp);
    ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
    return dateTime.toLocalDate();
  }
}
