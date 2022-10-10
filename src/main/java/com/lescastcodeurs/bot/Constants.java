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

  /**
   * Configuration key for GitHub repository ({@code owner/repo}).
   *
   * <p>For more information see the project README.
   */
  public static final String GITHUB_CONFERENCES_REPOSITORY = "github.conferences_repository";

  /**
   * Configuration key for GitHub criteria (e.g. {@code (France),Devoxx}).
   *
   * <p>For more information see the project README.
   */
  public static final String GITHUB_CONFERENCES_CRITERIA = "github.conferences_criteria";

  /** Address for the {@link com.lescastcodeurs.bot.GenerateShowNotesHandler}. */
  public static final String GENERATE_SHOW_NOTES_ADDRESS = "generate-show-notes";

  /** Address for the {@link com.lescastcodeurs.bot.GenerateShowNotesSummaryHandler}. */
  public static final String GENERATE_SHOW_NOTES_SUMMARY_ADDRESS = "generate-show-notes-summary";

  private Constants() {
    // prevent instantiation
  }
}
