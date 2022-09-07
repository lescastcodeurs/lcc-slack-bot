package com.lescastcodeurs.bot;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

/** Les cast codeurs podcast categories (by order of appearance in the show notes). */
public enum ShowNoteCategory {
  LANGUAGES("Langages", "lcc_lang"),
  LIBRARIES("Librairies", "lcc_lib"),
  INFRASTRUCTURE("Infrastructure", "lcc_infra"),
  CLOUD("Cloud", "lcc_cloud"),
  WEB("Web", "lcc_web"),
  DATA("Data", "lcc_data"),
  TOOLING("Outillage", "lcc_outil"),
  ARCHITECTURE("Architecture", "lcc_archi"),
  METHODOLOGIES("Méthodologies", "lcc_methodo"),
  SECURITY("Sécurité", "lcc_secu"),
  SOCIETY("Loi, société et organisation", "lcc_loi"),
  TOOL_OF_THE_EPISODE("Outils de l’épisode", "lcc_outil_ep"),
  BEGINNERS("Rubrique débutant", "lcc_debutant"),
  CONFERENCES("Conférences", "lcc_conf"),

  // special categories
  INCLUDE(
      "Messages inclus / Non catégorisés",
      "lcc_include"), // Fall-back for unknown categories / force message inclusion.
  EXCLUDE("Messages exclus", "lcc_exclude"); // Force message exclusion.

  private final String description;
  private final String reaction;

  /** Returns the category matching the given non-null label. */
  public static Optional<ShowNoteCategory> find(String reaction) {
    if (reaction != null) {
      for (ShowNoteCategory category : values()) {
        if (category.reaction.equals(reaction)) {
          return Optional.of(category);
        }
      }
    }

    return Optional.empty();
  }

  ShowNoteCategory(String description, String reaction) {
    this.description = requireNonNull(description);
    this.reaction = requireNonNull(reaction);
  }

  public String description() {
    return description;
  }

  public String reaction() {
    return reaction;
  }
}
