package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.Constants.SLACK_BOT_TOKEN;
import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

/**
 * A simple wrapper for this bot around Slacks {@link com.slack.api.methods.MethodsClient}.
 */
@ApplicationScoped
public final class SlackClient {

  private static final Logger LOG = getLogger(SlackClient.class);

  private final String botToken;

  public SlackClient(
    @ConfigProperty(name = SLACK_BOT_TOKEN) String botToken
  ) {
    this.botToken = requireNonNull(botToken);
  }

  public List<SlackMessage> history(String channel) {
    MethodsClient slack = Slack.getInstance().methods(botToken);
    var request = ConversationsHistoryRequest
      .builder()
      .channel(channel)
      .build();

    try {
      var response = slack.conversationsHistory(request);
      warnIfResponseContainsWarning(
        response.getWarning(),
        channel,
        response.getWarning()
      );

      if (response.isOk()) {
        return response.getMessages().stream().map(SlackMessage::new).toList();
      } else {
        throw error(channel, response.getError());
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (SlackApiException e) {
      throw new UncheckedSlackApiException(e);
    }
  }

  public void chatPostMessage(String channel, String message) {
    MethodsClient slack = Slack.getInstance().methods(botToken);
    var request = ChatPostMessageRequest
      .builder()
      .channel(channel)
      .text(message)
      .build();

    try {
      var response = slack.chatPostMessage(request);
      warnIfResponseContainsWarning(
        response.getWarning(),
        channel,
        response.getWarning()
      );

      if (!response.isOk()) {
        throw error(channel, response.getError());
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (SlackApiException e) {
      throw new UncheckedSlackApiException(e);
    }
  }

  private void warnIfResponseContainsWarning(
    String response,
    String channel,
    String response1
  ) {
    if (response != null) {
      LOG.warn(
        "chatPostMessage call to channel {} succeeded but returned a warning : {}",
        channel,
        response1
      );
    }
  }

  private UncheckedSlackApiException error(String channel, String response) {
    return new UncheckedSlackApiException(
      "chatPostMessage call to channel %s succeeded but returned an error : %s".formatted(
          channel,
          response
        )
    );
  }
}
