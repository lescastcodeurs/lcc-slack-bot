package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.Constants.GENERATE_SHOW_NOTES_SUMMARY_ADDRESS;
import static java.util.Objects.requireNonNull;

import com.lescastcodeurs.bot.slack.SlackClient;
import com.lescastcodeurs.bot.slack.SlackMentionEvent;
import com.lescastcodeurs.bot.slack.SlackThread;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

/** Handles {@link SlackBotAction#GENERATE_SHOW_NOTES_SUMMARY} commands. */
@ApplicationScoped
public final class GenerateShowNotesSummaryHandler extends LongTaskHandlerSupport<Void> {

  private final SlackClient slackClient;

  @Inject
  public GenerateShowNotesSummaryHandler(SlackClient slackClient) {
    this.slackClient = requireNonNull(slackClient);
  }

  @Override
  String description() {
    return "Generation of show notes summary";
  }

  @ConsumeEvent(value = GENERATE_SHOW_NOTES_SUMMARY_ADDRESS, blocking = true)
  public void consume(SlackMentionEvent event) throws InterruptedException {
    execute(
        event,
        () -> {
          List<SlackThread> threads = slackClient.history(event.channel(), false);
          ShowNotes notes = new ShowNotes("summary", threads, null, LocalDateTime.now());

          StringBuilder response = new StringBuilder();
          for (ShowNoteCategory category : ShowNoteCategory.values()) {
            List<ShowNote> categoryNotes = notes.notes(category.name());

            if (!categoryNotes.isEmpty()) {
              response.append("*");
              response.append(category.description());
              response.append("* (:");
              response.append(category.reaction());
              response.append(":)\n");

              int i = 1;
              for (ShowNote note : categoryNotes) {
                response.append(" ");
                response.append(i++);
                response.append("- ");
                response.append(note.rawText().lines().findFirst().orElseThrow());
                response.append(" (<");
                response.append(slackClient.permalink(event.channel(), note.timestamp()));
                response.append("|permalink>, order=");
                response.append(note.order());
                response.append(")\n");
              }

              response.append("\n");
            }
          }

          slackClient.chatPostMessage(event.channel(), event.replyTs(), response.toString());
          return null;
        },
        e ->
            slackClient.chatPostMessage(
                event.channel(),
                event.replyTs(),
                "Désolé, une erreur est survenue : %s - %s. Pour plus d'informations voir les logs du bot. Pensez à réessayer votre commande au cas où, notamment en cas de timeout."
                    .formatted(e.getClass().getSimpleName(), e.getMessage())));
  }
}
