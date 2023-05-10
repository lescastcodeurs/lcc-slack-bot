package com.lescastcodeurs.bot.conferences;

import static com.lescastcodeurs.bot.internal.StringUtils.isBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lescastcodeurs.bot.MarkdownSerializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

// Those fields are not used (but we still want this object to exactly reflect the expected JSON
// structure).
@JsonIgnoreProperties({"cfp", "status"})
@SuppressWarnings("java:S6218") // don't care
public record Conference(String name, String hyperlink, String location, long[] date, String misc)
    implements MarkdownSerializable {

  public static final long MIN_TIMESTAMP = Instant.EPOCH.toEpochMilli();
  public static final long MAX_TIMESTAMP = Long.MAX_VALUE - 1;
  private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("d MMMM uuuu");

  public boolean isValidCandidate(List<String> selectionCriteria, LocalDate date) {
    boolean result = hasValidData();
    result = result && isOnOrAfter(date);
    result = result && matchesAnyOf(selectionCriteria);
    return result;
  }

  public boolean hasValidData() {
    if (date == null || isBlank(name) || isBlank(hyperlink) || isBlank(location)) {
      return false;
    }

    if (date.length == 2) {
      return date[0] >= MIN_TIMESTAMP && date[1] <= MAX_TIMESTAMP && date[0] <= date[1];
    } else if (date.length == 1) {
      return date[0] >= MIN_TIMESTAMP && date[0] <= MAX_TIMESTAMP;
    }

    return false;
  }

  public boolean isOnOrAfter(LocalDate d) {
    long end = endDate();
    long now = d.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    return end >= now;
  }

  private long endDate() {
    return date.length == 1 ? date[0] : date[1];
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
  public String markdown(Locale locale) {
    LocalDate start = timestampToDate(date[0]);
    LocalDate end = timestampToDate(endDate());
    DateTimeFormatter formatter = FORMAT.withLocale(locale);

    String date;
    if (start.equals(end)) {
      date = end.format(formatter);
    } else if (start.getMonth() == end.getMonth()) {
      date = start.getDayOfMonth() + "-" + end.format(formatter);
    } else {
      date = start.format(formatter) + "-" + end.format(formatter);
    }

    return "- %s : [%s](%s) - %s %s%n".formatted(date, name, hyperlink, location, misc);
  }

  private static LocalDate timestampToDate(long timestamp) {
    Instant instant = Instant.ofEpochMilli(timestamp);
    ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
    return dateTime.toLocalDate();
  }
}
