package com.lescastcodeurs.bot.conferences;

import com.lescastcodeurs.bot.MarkdownSerializable;
import java.util.Locale;

public record NoConferenceMarkdown() implements MarkdownSerializable {

  public static final String MESSAGE =
      "La liste des conférences n'a pas pu être récupérée. Pour plus d'informations voir les logs du bot.";

  @Override
  public String markdown(Locale locale) {
    return MESSAGE;
  }
}
