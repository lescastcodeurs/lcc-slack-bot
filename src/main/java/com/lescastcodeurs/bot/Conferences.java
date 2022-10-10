package com.lescastcodeurs.bot;

import com.lescastcodeurs.bot.internal.StringUtils;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;

public record Conferences(String markdown, List<String> criteria) {

  private static final DateTimeFormatter ENGLISH_MONTH_FORMATTER =
      DateTimeFormatter.ofPattern("MMMM").withLocale(Locale.ENGLISH);
  private static final DateTimeFormatter FRENCH_MONTH_FORMATTER =
      DateTimeFormatter.ofPattern("MMMM").withLocale(Locale.FRENCH);

  public String markdown() {
    String rawMarkdown = markdown == null ? "" : markdown;

    StringBuilder cleanedMarkdown = new StringBuilder();

    Integer year = null;
    String month = null;
    for (String line : rawMarkdown.lines().toList()) {
      if (isYear(line)) {
        year = getYear(line);
      } else if (isMonth(line)) {
        month = getMonth(line);
      } else if (year != null && month != null && isSuitableConference(line)) {
        cleanedMarkdown.append(reformat(year, month, line));
        cleanedMarkdown.append("\n");
      }
    }

    return cleanedMarkdown.toString();
  }

  private static boolean isYear(String line) {
    return line.matches("^## \\d{4}$");
  }

  private static int getYear(String line) {
    return Integer.parseInt(line.substring(3, 7));
  }

  private static boolean isMonth(String line) {
    return line.matches("^###.+");
  }

  private static String getMonth(String line) {
    String month = line.substring(4);
    try {
      TemporalAccessor accessor = ENGLISH_MONTH_FORMATTER.parse(month);
      return FRENCH_MONTH_FORMATTER.format(accessor);
    } catch (DateTimeParseException e) {
      return month;
    }
  }

  private boolean isSuitableConference(String line) {
    if (criteria != null) {
      for (String criterion : criteria) {
        if (line.contains(criterion)) {
          return true;
        }
      }
    }

    return false;
  }

  private static String reformat(Integer year, String month, String line) {
    return line.replaceFirst(":", " %s %s :".formatted(month, year));
  }

  public boolean empty() {
    return StringUtils.isBlank(markdown());
  }
}
