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
   * URL of the JSON containing all conferences ({@code https://developers.events/all-events.json}).
   *
   * <p>For more information see the project README.
   */
  public static final String CONFERENCES_JSON_URL = "conferences.json_url";

  /**
   * Configuration key for the conferences selection criteria (e.g. {@code (France),Devoxx}).
   *
   * <p>For more information see the project README.
   */
  public static final String CONFERENCES_SELECTION_CRITERIA = "conferences.selection_criteria";

  /**
   * Configuration key for record date criterion (e.g. {@code
   * https://recording.zencastr.com/lescastcodeurs}).
   *
   * <p>For more information see the project README.
   */
  public static final String LCC_RECORD_DATE_CRITERION = "lcc.record_date_criterion";

  /** Address for the {@link com.lescastcodeurs.bot.GenerateShowNotesHandler}. */
  public static final String GENERATE_SHOW_NOTES_ADDRESS = "generate-show-notes";

  /** Address for the {@link com.lescastcodeurs.bot.GenerateShowNotesSummaryHandler}. */
  public static final String GENERATE_SHOW_NOTES_SUMMARY_ADDRESS = "generate-show-notes-summary";

  private Constants() {
    // prevent instantiation
  }
}
