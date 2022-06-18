package com.lescastcodeurs.bot;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Les cast codeurs podcast categories.
 */
public enum ShowNoteCategory {
  NEWS("news", "nouvelles", "nouvelle"),
  LANGUAGES("langages", "langage", "languages", "language", "langs", "lang"),
  LIBRARIES("libraries", "librairies", "librairie", "library", "libs", "lib"),
  INFRASTRUCTURE("infrastructure", "infra"),
  CLOUD("cloud"),
  WEB("web"),
  DATA("data"),
  TOOLING("outillage", "tooling"),
  ARCHITECTURE("architecture", "architectures", "archi", "arch"),
  METHODOLOGIES(
    "methodologies",
    "methodologie",
    "methodology",
    "methode",
    "methodo"
  ),
  SECURITY("securite", "security", "secure", "secu", "sec"),
  SOCIETY(
    "loi",
    "law",
    "societe",
    "society",
    "organisation",
    "organization",
    "orga",
    "org"
  ),
  EPISODE_TOOL("outils", "outil", "tools", "tool"),
  BEGINNERS("debutants", "debutant", "beginners", "beginner"),
  CONFERENCE("conferences", "conference", "conf");

  private static String normalize(String s) {
    String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
    normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
    normalized = normalized.toLowerCase(Locale.ROOT);
    normalized = normalized.trim();
    return normalized;
  }

  private final Set<String> labels;

  /**
   * Returns the category matching the given non-null label, or {@link #NEWS} if the label does not match any category.
   * <p>
   * Note that label are case-sensitive and accents-insensitive.
   */
  public static Optional<ShowNoteCategory> find(String label) {
    if (label != null) {
      String normalized = normalize(label);

      for (ShowNoteCategory category : values()) {
        if (category.labels.contains(normalized)) {
          return Optional.of(category);
        }
      }
    }

    return Optional.empty();
  }

  ShowNoteCategory(String... labels) {
    this.labels =
      Set.copyOf(
        Arrays.stream(labels).map(ShowNoteCategory::normalize).toList()
      );
  }

  public Set<String> getLabels() {
    return labels;
  }
}
