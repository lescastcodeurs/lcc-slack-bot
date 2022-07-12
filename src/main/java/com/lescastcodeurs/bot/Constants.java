package com.lescastcodeurs.bot;

public final class Constants {

  /**
   * Configuration key for the app-level token ({@code xapp-XXX}).
   *
   * <p>For more information see the project README.
   */
  public static final String SLACK_APP_TOKEN = "slack.app.token";

  /**
   * Configuration key for the bot user OAuth token ({@code xoxb-XXX}).
   *
   * <p>For more information see the project README.
   */
  public static final String SLACK_BOT_TOKEN = "slack.bot.token";

  /**
   * Configuration key for GitHub token ({@code xoxb-XXX}).
   *
   * <p>For more information see the project README.
   */
  public static final String GITHUB_TOKEN = "github.token";

  /**
   * Configuration key for GitHub repository ({@code owner/repo}).
   *
   * <p>For more information see the project README.
   */
  public static final String GITHUB_REPOSITORY = "github.repository";

  /** Address for the {@link com.lescastcodeurs.bot.GenerateShowNotesHandler}. */
  public static final String GENERATE_SHOW_NOTES_ADDRESS = "generate-show-notes";

  private Constants() {
    // prevent instantiation
  }
}
