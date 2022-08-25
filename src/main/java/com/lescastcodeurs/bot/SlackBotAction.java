package com.lescastcodeurs.bot;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public enum SlackBotAction {
  ARE_YOU_THERE(
      1,
      List.of("present", "there", "tu es la", "es-tu la"),
      "Yep, je suis tout ouïes.",
      List.of("@lcc, es-tu présent ?", "@lcc, are you there?"),
      "Permet de vérifier que je suis à l'écoute."),

  HELP(
      10,
      List.of("aide", "help"),
      null,
      List.of("@lcc, à l'aide !", "@lcc, help me please !"),
      "affiche l'aide.") {
    @Override
    public String response() {
      return """
        Voici les commandes auxquelles je peux répondre (la ponctuation, les accents, la casse ainsi que la présence de mots ou caractères supplémentaires sont ignorés).

        %s
        """
          .formatted(
              stream(SlackBotAction.values())
                  .map(SlackBotAction::help)
                  .filter(Objects::nonNull)
                  .map(Object::toString)
                  .collect(joining("\n\n")));
    }
  },

  SHOW_CATEGORIES(
      20,
      List.of("category", "categorie", "label", "libelle", "tag"),
      """
      Les catégories et leurs libellés sont :
      • %s
      • Non catégorisé: laissé sans catégorie ou catégorisé avec un libellé inconnu
      """
          .formatted(
              stream(ShowNoteCategory.values())
                  .filter(c -> c != ShowNoteCategory.NEWS)
                  .map(
                      c ->
                          "%s : `%s` (%s)"
                              .formatted(
                                  c.description(),
                                  c.mainLabel(),
                                  c.alternateLabels().stream()
                                      .map("`%s`"::formatted)
                                      .collect(joining(", "))))
                  .collect(joining("\n• "))),
      List.of("@lcc, montre-moi les catégories.", "@lcc, show me the categories."),
      "Affiche la liste des catégories avec leurs libellés associés (multilignes, ordre de déclaration)."),

  GENERATE_SHOW_NOTES(
      3,
      List.of("genere", "generate"),
      "OK, je suis sur le coup !",
      List.of("@lcc, génère les show notes.", "@lcc, generate show notes."),
      """
      Génère les notes de l'épisode à partir des messages de ce channel et publie le résultat sur GitHub. Les show notes peuvent être publiées plusieurs fois: le fichier markdown est alors mis à jour. À noter :
      • Un channel Slack doit être dédié à un seul épisode.
      • Un thread de messages est reporté dans les show notes si son premier message contient au moins un lien.
      • Les réponses aux liens peuvent être de simples phrases comme des listes.
      • La formatage suivant est conservé : *gras*, _italique_, ~barré~, `code` et citations (sur le premier message uniquement).
      • Les liens peuvent être catégorisés à l'aide de libellés (ex. `Nouveau JEP https://www.java.com (lang)`). Les catégories supportées peuvent être listées grâce à la commande dédiée (`@lcc, affiche les catégories.`).
      """,
      Constants.GENERATE_SHOW_NOTES_ADDRESS),

  UNKNOWN(999, List.of(), null, List.of(), null) {
    @Override
    public String response() {
      return "Désolé, je n'ai pas compris la commande. %s".formatted(HELP.response());
    }

    @Override
    protected boolean canReplyTo(String request) {
      return true;
    }

    @Override
    public String help() {
      return null;
    }
  };

  /** Determine the order in which the commands will be evaluated during the "guessing process". */
  private final int guessOrder;

  /** A list of words used to determine the action associated to a command. */
  protected final List<String> keywords;

  /** The command response message. */
  private final String response;

  /** Examples of command to use to trigger the action. */
  private final List<String> usages;

  /** Description of the action. */
  private final String description;

  /**
   * Address of the handler to which the slack event must be sent if further processing is required
   * to fulfil the action. {@code null} if no further processing is required.
   */
  private final String handlerAddress;

  SlackBotAction(
      int guessOrder,
      List<String> keywords,
      String response,
      List<String> usages,
      String description,
      String handlerAddress) {
    this.guessOrder = guessOrder;
    this.keywords = keywords;
    this.response = response;
    this.usages = usages;
    this.description = description;
    this.handlerAddress = handlerAddress;
  }

  SlackBotAction(
      int guessOrder,
      List<String> keywords,
      String response,
      List<String> usages,
      String description) {
    this(guessOrder, keywords, response, usages, description, null);
  }

  public static SlackBotAction guess(String command) {
    return stream(values())
        .sorted(comparingInt(c -> c.guessOrder))
        .filter(c -> c.canReplyTo(command))
        .findFirst()
        .orElse(UNKNOWN);
  }

  protected boolean canReplyTo(String request) {
    if (request != null) {
      String normalizedRequest = StringUtils.normalize(request);
      for (String keyword : keywords) {
        if (normalizedRequest.contains(keyword)) {
          return true;
        }
      }
    }

    return false;
  }

  public List<String> keywords() {
    return keywords;
  }

  public List<String> usages() {
    return usages;
  }

  public String response() {
    return response;
  }

  public Optional<String> handlerAddress() {
    return Optional.ofNullable(handlerAddress);
  }

  public String help() {
    String commands = keywords.stream().map("*%s*"::formatted).collect(joining(" | "));
    String examples = usages.stream().map("`%s`"::formatted).collect(joining(", "));
    return "%s (%s) : %s".formatted(commands, examples, description);
  }
}
