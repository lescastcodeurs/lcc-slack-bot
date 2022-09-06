package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.ShowNoteCategory.EXCLUDE;
import static com.lescastcodeurs.bot.ShowNoteCategory.INCLUDE;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.joining;

import com.lescastcodeurs.bot.internal.StringUtils;
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
      Les catégories et leur réaction associée sont :
      • %s

      La réaction `:lcc_include:` (:lcc_include:) correspond à la catégorie _Non catégorisées_. Elle permet aussi de forcer l'inclusion d'un message (show note comme réponse).

      La réaction `:lcc_exclude:` (:lcc_exclude:) n'est pas une catégorie : elle permet de forcer l'exclusion d'un message (show note comme réponse). Cette réaction est prioritaire par rapport à toutes les autres réactions.
      Si plusieurs réactions (autres que `:lcc_exclude:`) sont utilisées, c'est la dernière réactions retournée par l'API Slack qui est prise en compte. Ça semble être la dernière réaction ajoutée, mais ça n'est malheureusement <https://forums.slackcommunity.com/s/question/0D53a00008kB81SCAS/how-are-messagereactions-sorted-|pas garanti>.

      Les réactions `:lcc_1:` (:lcc_1:), `:lcc_2:` (:lcc_2:) ... `:lcc_9:` (:lcc_9:) ne sont pas des catégories. Elles permettent d'ordonner les messages :
       - au sein de leur catégories pour les show notes,
       - au sein de la show note pour les réponses).
      Le tri se fait sur la réaction, puis selon l'ordre dans le channel ou le thread (c'est l'ordre par défaut, il est chronologique par rapport à la date de création du message).
      """
          .formatted(
              stream(ShowNoteCategory.values())
                  .filter(c -> c != INCLUDE && c != EXCLUDE)
                  .map(
                      c ->
                          "%s : `%s` (:%s:)".formatted(c.description(), c.reaction(), c.reaction()))
                  .collect(joining("\n• "))),
      List.of("@lcc, montre-moi les catégories.", "@lcc, show me the categories."),
      "Affiche la liste des catégories avec leur réaction associée."),

  GENERATE_SHOW_NOTES(
      3,
      List.of("genere", "generate"),
      "OK, je suis sur le coup !",
      List.of("@lcc, génère les show notes.", "@lcc, generate show notes."),
      """
      Génère les notes de l'épisode à partir des show notes contenues dans ce channel et publie le résultat sur GitHub. À noter :
       • Un channel Slack doit être dédié à un seul épisode.
       • Le nom du channel est utilisé pour construire le nom du fichier sur GitHub. Si ce nom contient un ou plusieurs nombres, le premier nombre est utilisé comme numéro de l'épisode.
       • Chaque thread de messages correspond est potentiellement une show note. Le premier message est généralement un lien (ça n'est néanmoins pas obligatoire).
       • Les réponses au thread sont les commentaires sur le lien. Ces réponses seront présentées sous la forme d'une liste sous le premier message du thread.
       • Les show notes peuvent être catégorisées à l'aide de <https://slack.com/intl/fr-fr/help/articles/202931348-Utilisez-les-%C3%A9mojis-et-les-r%C3%A9actions|réactions> utilisant les émojis personnalisés dont le nom démarre par `lcc_`. Elles seront alors écrites directement dans la bonne catégorie. Les catégories supportées peuvent être listées grâce à la commande dédiée (`@lcc, affiche les catégories.`).
       • L'utilisation de la réactions `:lcc_exclude:` (:lcc_exclude:) est prioritaire sur toutes les autres réactions (sinon c'est la dernière réaction qui gagne).
       • L'utilisation de <https://slack.com/intl/fr-fr/help/articles/205240127-Utiliser-les-mentions-dans-Slack|mentions> exclue généralement les messages des show notes.
       • Un thread de messages est considéré comme show note que si :
         ◦ son premier message a été écrit par un utilisateur (c-à-d pas un bot ni une application Slack),
         ◦ son premier message est catégorisé grâce à une autre réaction que `:lcc_exclude:` (:lcc_exclude:),
         ◦ ou son premier message contient au moins un lien et aucune mention (auquel cas il apparait dans la catégorie _Non catégorisées_).
       • Les réponses aux threads peuvent être de simples phrases comme des listes ou même des sous-listes. Une réponse est reportée dans les show notes que si :
         ◦ elle a été écrite par un utilisateur (c-à-d pas un bot ni une application Slack),
         ◦ elle n'a pas de réaction `:lcc_exclude:` (:lcc_exclude:),
         ◦ elle a la réaction `:lcc_include:` (:lcc_include:),
         ◦ ou elle ne contient aucune mention.
       • Les show notes et les réponses peuvent être réordonnées à l'aide des réactions `:lcc_1:` (:lcc_1:), `:lcc_2:` (:lcc_2:) ... `:lcc_9:` (:lcc_9:). Le tri se fait sur la réaction, puis selon l'ordre dans le channel ou le thread (c'est l'ordre par défaut, il est chronologique par rapport à la date de création du message).
       • La formatage suivant est conservé : *gras*, _italique_, ~barré~, `code`. Les citations sont aussi possibles, mais uniquement sur le premier message du thread.
       • Les retours chariot ne sont conservés que sur le premier message du thread, ou quand les réponses sont organisées sous la forme d'une liste.
       • Les show notes peuvent être publiées plusieurs fois: le fichier markdown est alors mis à jour.
      """,
      Constants.GENERATE_SHOW_NOTES_ADDRESS),

  UNKNOWN(
      999,
      List.of(),
      "Désolé, je n'ai pas compris la commande. Pour voir l'aide utilisez la commande dédiée (`@lcc, à l'aide !`)",
      List.of(),
      null) {
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
