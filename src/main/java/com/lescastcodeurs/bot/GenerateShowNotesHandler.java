package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.Constants.GENERATE_SHOW_NOTES_ADDRESS;
import static com.lescastcodeurs.bot.Constants.SLACK_BOT_TOKEN;
import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.model.event.AppMentionEvent;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.vertx.ConsumeEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

/**
 * Handles {@link SlackBotCommand#GENERATE_SHOW_NOTES} commands.
 */
@ApplicationScoped
public final class GenerateShowNotesHandler {

  private static final Logger LOG = getLogger(GenerateShowNotesHandler.class);

  private final Template notes;
  private final String botToken;

  @Inject
  public GenerateShowNotesHandler(
    @Location("show-notes.md") Template notes,
    @ConfigProperty(name = SLACK_BOT_TOKEN) String botToken
  ) {
    this.notes = requireNonNull(notes);
    this.botToken = requireNonNull(botToken);
  }

  @ConsumeEvent(GENERATE_SHOW_NOTES_ADDRESS)
  public void consume(AppMentionEvent event) {
    String channel = event.getChannel();

    try {
      MethodsClient slack = Slack.getInstance().methods(botToken);

      ConversationsHistoryResponse history = slack.conversationsHistory(
        ConversationsHistoryRequest
          .builder()
          .channel(event.getChannel())
          .build()
      );
      ShowNotes data = new ShowNotes(history);
      String result = notes.render(data);

      slack.chatPostMessage(
        ChatPostMessageRequest.builder().channel(channel).text(result).build()
      );
      LOG.info("Show notes generated for channel {}", channel);
    } catch (Exception e) {
      LOG.error(
        "An unexpected error occurred while generating show notes for channel {}.",
        channel,
        e
      );
    }
  }
}
