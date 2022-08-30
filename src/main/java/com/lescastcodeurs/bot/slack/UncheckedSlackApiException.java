package com.lescastcodeurs.bot.slack;

import static java.util.Objects.requireNonNull;

import com.slack.api.methods.SlackApiException;

/**
 * This class as two purposes : - wraps {@link com.slack.api.methods.SlackApiException} with an
 * unchecked exception, - provide a way to create custom Slack-related exceptions.
 */
public class UncheckedSlackApiException extends RuntimeException {

  public UncheckedSlackApiException(String message) {
    super(requireNonNull(message));
  }

  public UncheckedSlackApiException(SlackApiException cause) {
    super(requireNonNull(cause));
  }

  @Override
  public synchronized SlackApiException getCause() {
    return (SlackApiException) super.getCause();
  }
}
