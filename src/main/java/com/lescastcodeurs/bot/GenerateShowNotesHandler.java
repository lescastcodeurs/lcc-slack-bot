package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.Constants.GENERATE_SHOW_NOTES_ADDRESS;
import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

import com.slack.api.model.event.AppMentionEvent;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.vertx.ConsumeEvent;
import java.io.UncheckedIOException;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 * Handles {@link SlackBotCommand#GENERATE_SHOW_NOTES} commands.
 */
@ApplicationScoped
public final class GenerateShowNotesHandler {

  private static final Logger LOG = getLogger(GenerateShowNotesHandler.class);

  private final Template notes;
  private final SlackClient client;

  @Inject
  public GenerateShowNotesHandler(
    SlackClient client,
    @Location("show-notes.md") Template notes
  ) {
    this.notes = requireNonNull(notes);
    this.client = requireNonNull(client);
  }

  @ConsumeEvent(GENERATE_SHOW_NOTES_ADDRESS)
  public void consume(AppMentionEvent event) {
    String channel = event.getChannel();

    try {
      List<SlackMessage> messages = client.history(channel);
      client.chatPostMessage(channel, notes.render(new ShowNotes(messages)));
      LOG.info("Show notes generated for channel {}", channel);
    } catch (UncheckedIOException | UncheckedSlackApiException e) {
      LOG.error(
        "An unexpected error occurred while generating show notes for channel {}.",
        channel,
        e
      );
    }
  }
}
