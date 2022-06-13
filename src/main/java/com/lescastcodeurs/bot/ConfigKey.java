package com.lescastcodeurs.bot;

public final class ConfigKey {

  /**
   * Configuration key for the app-level token ({@code xapp-XXX}).
   * <p>
   * For more information see the project README.
   */
  public static final String SLACK_APP_TOKEN = "slack.app.token";

  /**
   * Configuration key for the bot user OAuth token ({@code xoxb-XXX}).
   * <p>
   * For more information see the project README.
   */
  public static final String SLACK_BOT_TOKEN = "slack.bot.token";

  private ConfigKey() {
    // prevent instantiation
  }
}
