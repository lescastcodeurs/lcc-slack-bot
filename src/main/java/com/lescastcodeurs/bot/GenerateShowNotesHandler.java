package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.Constants.GENERATE_SHOW_NOTES_ADDRESS;
import static com.lescastcodeurs.bot.internal.StringUtils.asFilename;
import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

import com.lescastcodeurs.bot.github.GitHubClient;
import com.lescastcodeurs.bot.internal.Stopwatch;
import com.lescastcodeurs.bot.slack.SlackClient;
import com.lescastcodeurs.bot.slack.SlackMentionEvent;
import com.lescastcodeurs.bot.slack.SlackThread;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.vertx.ConsumeEvent;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;

/** Handles {@link SlackBotAction#GENERATE_SHOW_NOTES} commands. */
@ApplicationScoped
public final class GenerateShowNotesHandler {

  private static final Logger LOG = getLogger(GenerateShowNotesHandler.class);

  private final Template notes;
  private final SlackClient slackClient;
  private final GitHubClient gitHubClient;

  @Inject
  public GenerateShowNotesHandler(
      SlackClient slackClient,
      GitHubClient gitHubClient,
      @Location("show-notes.md") Template notes) {
    this.notes = requireNonNull(notes);
    this.slackClient = requireNonNull(slackClient);
    this.gitHubClient = requireNonNull(gitHubClient);
  }

  @ConsumeEvent(GENERATE_SHOW_NOTES_ADDRESS)
  public void consume(SlackMentionEvent event) throws InterruptedException {
    Stopwatch stopwatch = new Stopwatch();
    String channel = event.channel();
    String timestamp = event.ts();
    String threadTs = event.replyTs();

    try {
      LOG.info(
          "Starting generation of show notes for message {} in channel {}", timestamp, channel);
      String channelName = slackClient.name(channel);
      List<SlackThread> threads = slackClient.history(channel);

      String filename = asFilename(channelName, "md");
      String content = notes.render(new ShowNotes(channelName, threads));
      String showNoteUrl = gitHubClient.createOrUpdateFile(filename, content);

      slackClient.chatPostMessage(
          channel,
          threadTs,
          "C'est fait, les show notes sont disponibles sur <%s>.".formatted(showNoteUrl));
      LOG.info(
          "Show notes generation succeeded for for message {} in channel {}, took {}",
          timestamp,
          channel,
          stopwatch);
    } catch (RuntimeException e) {
      LOG.error(
          "Show notes generation failed for for message {} in channel {}, took {}",
          timestamp,
          channel,
          stopwatch,
          e);
      slackClient.chatPostMessage(
          channel,
          threadTs,
          "Une erreur est survenue lors de la génération des show notes : %s - %s. Pour plus d'infos voir les logs du bot."
              .formatted(e.getClass().getSimpleName(), e.getMessage()));
    }
  }
}
