package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.Constants.SLACK_BOT_TOKEN;
import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.SlackApiTextResponse;
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

  public SlackClient(@ConfigProperty(name = SLACK_BOT_TOKEN) String botToken) {
    this.botToken = requireNonNull(botToken);
  }

  /**
   * See <a href="https://api.slack.com/methods/conversations.history">conversations.history</a>.
   */
  public List<SlackMessage> history(String channel) {
    MethodsClient slack = Slack.getInstance().methods(botToken);

    return SlackApi
      .check(() ->
        slack.conversationsHistory(
          ConversationsHistoryRequest.builder().channel(channel).build()
        )
      )
      .getMessages()
      .stream()
      .map(SlackMessage::new)
      .toList();
  }

  /**
   * See <a href="https://api.slack.com/methods/chat.postMessage">chat.postMessage</a>.
   */
  public void chatPostMessage(String channel, String message) {
    MethodsClient slack = Slack.getInstance().methods(botToken);

    SlackApi.check(() ->
      slack.chatPostMessage(
        ChatPostMessageRequest.builder().channel(channel).text(message).build()
      )
    );
  }

  /**
   * Wrap call to Slack API and handle error / warning.
   *
   * @param <R> the response type
   */
  @FunctionalInterface
  private interface SlackApi<R extends SlackApiTextResponse> {
    R call() throws IOException, SlackApiException;

    static <R extends SlackApiTextResponse> R check(SlackApi<R> api) {
      try {
        R response = api.call();
        handleWarning(response);
        handleError(response);
        return response;
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      } catch (SlackApiException e) {
        throw new UncheckedSlackApiException(e);
      }
    }

    static void handleWarning(SlackApiTextResponse response) {
      if (response.getWarning() != null) {
        LOG.warn(
          "slack {} contains a warning : {}",
          response.getClass().getSimpleName(),
          response.getWarning()
        );
      }
    }

    static void handleError(SlackApiTextResponse response) {
      if (!response.isOk()) {
        throw new UncheckedSlackApiException(
          "slack %s contains an error : %s".formatted(
              response.getClass().getSimpleName(),
              response.getError()
            )
        );
      }
    }
  }
}
