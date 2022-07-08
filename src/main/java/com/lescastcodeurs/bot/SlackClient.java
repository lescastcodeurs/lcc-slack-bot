package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.Constants.SLACK_BOT_TOKEN;
import static java.util.Comparator.*;
import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.SlackApiTextResponse;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.request.conversations.ConversationsInfoRequest;
import com.slack.api.methods.request.conversations.ConversationsRepliesRequest;
import com.slack.api.model.Message;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

/**
 * A simple wrapper for this bot around Slacks {@link com.slack.api.methods.MethodsClient}.
 */
@ApplicationScoped
public final class SlackClient {

  /**
   * Chronological comparator of {@link Message}s.
   */
  private static final Comparator<Message> CHRONOLOGICAL = comparing(
    Message::getTs,
    nullsLast(naturalOrder())
  );

  /**
   * Returns {@code true} if a {@link Message} is a reply, {@code false} otherwise.
   */
  private static final Predicate<Message> IS_REPLY = r ->
    r.getTs() == null ||
    r.getThreadTs() == null ||
    !r.getTs().equals(r.getThreadTs());

  private static final Logger LOG = getLogger(SlackClient.class);

  private final String botToken;

  public SlackClient(@ConfigProperty(name = SLACK_BOT_TOKEN) String botToken) {
    this.botToken = requireNonNull(botToken);
  }

  /**
   * Get the given channel name.
   */
  public String name(String channel) {
    MethodsClient slack = Slack.getInstance().methods(botToken);

    return SlackApi
      .check(() ->
        slack.conversationsInfo(
          ConversationsInfoRequest.builder().channel(channel).build()
        )
      )
      .getChannel()
      .getName();
  }

  /**
   * Fetch the messages (and their replies) of the given channel in chronological order.
   *
   * @see #replies(String, String)
   * @see <a href="https://api.slack.com/methods/conversations.history">conversations.history</a>.
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
      .sorted(CHRONOLOGICAL)
      .map(m -> new SlackMessage(m, replies(channel, m))) // note: oddly, m.getChannel() is null, so it should be given explicitly
      .toList();
  }

  private List<String> replies(String channel, Message message) {
    Integer replyCount = message.getReplyCount();

    if (replyCount != null && replyCount > 0) {
      return replies(channel, message.getTs());
    }

    return List.of();
  }

  /**
   * Fetch the replies of the given message in chronological order.
   *
   * @see <a href="https://api.slack.com/methods/conversations.replies">conversations.replies</a>.
   */
  public List<String> replies(String channel, String ts) {
    MethodsClient slack = Slack.getInstance().methods(botToken);
    return SlackApi
      .check(() ->
        slack.conversationsReplies(
          ConversationsRepliesRequest.builder().channel(channel).ts(ts).build()
        )
      )
      .getMessages()
      .stream()
      .filter(IS_REPLY)
      .sorted(CHRONOLOGICAL)
      .map(Message::getText)
      .toList();
  }

  /**
   * Post a message in the given channel.
   *
   * @see <a href="https://api.slack.com/methods/chat.postMessage">chat.postMessage</a>.
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
          "slack %s contains an error : %s (needed = %s, provided = %s)".formatted(
              response.getClass().getSimpleName(),
              response.getError(),
              response.getNeeded(),
              response.getProvided()
            )
        );
      }
    }
  }
}
