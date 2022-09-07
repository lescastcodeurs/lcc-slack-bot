package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.Constants.GENERATE_SHOW_NOTES_ADDRESS;
import static com.lescastcodeurs.bot.internal.StringUtils.asFilename;
import static java.util.Objects.requireNonNull;

import com.lescastcodeurs.bot.github.GitHubClient;
import com.lescastcodeurs.bot.slack.SlackClient;
import com.lescastcodeurs.bot.slack.SlackMentionEvent;
import com.lescastcodeurs.bot.slack.SlackThread;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.vertx.ConsumeEvent;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/** Handles {@link SlackBotAction#GENERATE_SHOW_NOTES} commands. */
@ApplicationScoped
public final class GenerateShowNotesHandler extends LongTaskHandlerSupport<Void> {

  private final Template notes;
  private final SlackClient slackClient;
  private final GitHubClient gitHubClient;

  @Inject
  public GenerateShowNotesHandler(
      SlackClient slackClient,
      GitHubClient gitHubClient,
      @Location("show-notes.md") Template notes) {
    this.slackClient = requireNonNull(slackClient);
    this.gitHubClient = requireNonNull(gitHubClient);
    this.notes = requireNonNull(notes);
  }

  @Override
  String description() {
    return "generation of show notes";
  }

  @ConsumeEvent(GENERATE_SHOW_NOTES_ADDRESS)
  public void consume(SlackMentionEvent event) throws InterruptedException {
    execute(
        event,
        () -> {
          String channelName = slackClient.name(event.channel());
          List<SlackThread> threads = slackClient.history(event.channel(), true);

          String filename = asFilename(channelName, "md");
          String content = notes.render(new ShowNotes(channelName, threads));
          String showNoteUrl = gitHubClient.createOrUpdateFile(filename, content);

          slackClient.chatPostMessage(
              event.channel(),
              event.replyTs(),
              "C'est fait, les show notes sont disponibles sur <%s>.".formatted(showNoteUrl));
          return null;
        },
        e ->
            slackClient.chatPostMessage(
                event.channel(),
                event.replyTs(),
                "Désolé, une erreur est survenue : %s - %s. Pour plus d'infos voir les logs du bot."
                    .formatted(e.getClass().getSimpleName(), e.getMessage())));
  }
}
