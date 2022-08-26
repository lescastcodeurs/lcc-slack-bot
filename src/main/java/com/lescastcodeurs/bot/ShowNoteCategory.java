package com.lescastcodeurs.bot;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** Les cast codeurs podcast categories (by order of appearance in the show notes). */
public enum ShowNoteCategory {
  LANGUAGES("Langages", "lang", "langage", "langages", "langs", "language", "languages"),
  LIBRARIES("Librairies", "lib", "libs", "libraries", "librairies", "librairie", "library"),
  INFRASTRUCTURE("Infrastructure", "infra", "infrastructure"),
  CLOUD("Cloud", "cloud", "iaas"),
  WEB("Web", "web", "www"),
  DATA("Data", "data", "db"),
  TOOLING("Outillage", "outil", "outils", "tool", "tools", "outillage", "tooling"),
  ARCHITECTURE("Architecture", "archi", "arch", "architecture", "architectures"),
  METHODOLOGIES(
      "Méthodologies",
      "methodo",
      "metodo",
      "methode",
      "methodologie",
      "methodologies",
      "methodology"),
  SECURITY("Sécurité", "secu", "sec", "securite", "security", "secure"),
  SOCIETY(
      "Loi, société et organisation",
      "loi",
      "law",
      "societe",
      "society",
      "org",
      "orga",
      "organisation",
      "organization"),
  TOOL_OF_THE_EPISODE("Outils de l’épisode", "outil-ep", "outil-episode"),
  BEGINNERS("Rubrique débutant", "debutant", "debutants", "beginner", "beginners"),
  CONFERENCES("Conférences", "conf", "conferences", "conference"),
  // This category must not be used in template.
  IGNORED("Ignoré", "ignore", "ignorer", "ignored", "exclu", "exclude", "exclure"),
  // Fall-back for unrecognized categories.
  NEWS("Non catégorisé", "news", "nouvelles", "nouvelle");

  private final String description;
  private final List<String> labels;

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
        if (category.labels().contains(normalized)) {
          return Optional.of(category);
        }
      }
    }

    return Optional.empty();
  }

  ShowNoteCategory(String description, String... labels) {
    this.description = requireNonNull(description);
    this.labels = List.copyOf(Arrays.stream(labels).map(StringUtils::normalize).toList());

    if (this.labels.size() < 2) {
      throw new IllegalArgumentException(
          "at least two labels are required (formatting help is easier with this constraint)");
    }
  }

  public String description() {
    return description;
  }

  public String mainLabel() {
    return labels.get(0);
  }

  public List<String> alternateLabels() {
    return labels.subList(1, labels.size());
  }

  public List<String> labels() {
    return labels;
  }
}
