package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.Constants.GENERATE_SHOW_NOTES_ADDRESS;
import static com.lescastcodeurs.bot.StringUtils.asFilename;
import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

import com.lescastcodeurs.bot.slack.SlackClient;
import com.lescastcodeurs.bot.slack.SlackThread;
import com.slack.api.model.event.AppMentionEvent;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.vertx.ConsumeEvent;
import java.io.UncheckedIOException;
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
  public void consume(AppMentionEvent event) throws InterruptedException {
    String channel = event.getChannel();

    try {
      String channelName = slackClient.name(channel);
      List<SlackThread> threads = slackClient.history(channel);

      String filename = asFilename(channelName, "md");
      String content = notes.render(new ShowNotes(threads));
      String showNoteUrl = gitHubClient.createOrUpdateFile(filename, content);

      slackClient.chatPostMessage(
          channel, "C'est fait, les show notes sont disponibles sur <" + showNoteUrl + ">.");
      LOG.info("Show notes generated for channel {}", channel);
    } catch (UncheckedIOException | UncheckedSlackApiException | GitHubApiException e) {
      LOG.error(
          "An unexpected error occurred while generating show notes for channel {}.", channel, e);
    }
  }
}
