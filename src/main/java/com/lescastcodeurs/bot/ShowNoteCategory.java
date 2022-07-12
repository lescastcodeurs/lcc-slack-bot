package com.lescastcodeurs.bot;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/** Les cast codeurs podcast categories. */
public enum ShowNoteCategory {
  NEWS("Non catégorisées", "news", "nouvelles", "nouvelle"),
  LANGUAGES("Langages", "langages", "langage", "languages", "language", "langs", "lang"),
  LIBRARIES("Librairies", "libraries", "librairies", "librairie", "library", "libs", "lib"),
  INFRASTRUCTURE("Infrastructure", "infrastructure", "infra"),
  CLOUD("Cloud", "cloud"),
  WEB("Web", "web"),
  DATA("Data", "data"),
  TOOLING("Outillage", "outillage", "tooling"),
  ARCHITECTURE("Architecture", "architecture", "architectures", "archi", "arch"),
  METHODOLOGIES(
      "Méthodologies", "methodologies", "methodologie", "methodology", "methode", "methodo"),
  SECURITY("Sécurité", "securite", "security", "secure", "secu", "sec"),
  SOCIETY(
      "Loi, société et organisation",
      "loi",
      "law",
      "societe",
      "society",
      "organisation",
      "organization",
      "orga",
      "org"),
  EPISODE_TOOL("Outils de l’épisode", "outils", "outil", "tools", "tool"),
  BEGINNERS("Rubrique débutant", "debutants", "debutant", "beginners", "beginner"),
  CONFERENCE("Conférences", "conferences", "conference", "conf");

  private final String description;
  private final Set<String> labels;

  /**
   * Returns the category matching the given non-null label, or {@link #NEWS} if the label does not
   * match any category.
   *
   * <p>Note that label are case-sensitive and accents-insensitive.
   */
  public static Optional<ShowNoteCategory> find(String label) {
    if (label != null) {
      String normalized = StringUtils.normalize(label);

      for (ShowNoteCategory category : values()) {
        if (category.labels.contains(normalized)) {
          return Optional.of(category);
        }
      }
    }

    return Optional.empty();
  }

  ShowNoteCategory(String description, String... labels) {
    this.description = requireNonNull(description);
    this.labels = Set.copyOf(Arrays.stream(labels).map(StringUtils::normalize).toList());
  }

  public String description() {
    return description;
  }

  public Set<String> getLabels() {
    return labels;
  }
}
