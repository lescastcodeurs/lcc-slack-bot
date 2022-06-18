package com.lescastcodeurs.bot;

import java.text.Normalizer;
import java.util.Locale;

public final class StringUtils {

  private StringUtils() {
    // prevent instantiation
  }

  public static String normalize(String s) {
    String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
    normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
    normalized = normalized.toLowerCase(Locale.ROOT);
    normalized = normalized.trim();
    return normalized;
  }
}
