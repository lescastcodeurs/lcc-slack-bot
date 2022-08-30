package com.lescastcodeurs.bot.internal;

import java.text.Normalizer;
import java.util.Locale;

public final class StringUtils {

  private StringUtils() {
    // prevent instantiation
  }

  public static boolean isBlank(String s) {
    return s == null || s.isBlank();
  }

  public static boolean isNotBlank(String s) {
    return !isBlank(s);
  }

  public static String normalize(String s) {
    String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
    normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
    normalized = normalized.toLowerCase(Locale.ROOT);
    normalized = normalized.trim();
    return normalized;
  }

  public static String asFilename(String base, String extension) {
    String normalizedBase = StringUtils.normalize(base).replaceAll("[^a-z\\d]+", "-");
    normalizedBase = normalizedBase.isEmpty() ? "-" : normalizedBase;
    String normalizedExtension =
        extension.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z\\d]+", "");
    return normalizedBase + "." + normalizedExtension;
  }
}
