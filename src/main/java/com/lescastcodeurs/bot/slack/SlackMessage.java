package com.lescastcodeurs.bot.slack;

import static com.lescastcodeurs.bot.StringUtils.isNotBlank;

import com.slack.api.model.Message;
import java.util.regex.Pattern;

/** Hold required information from a Slack message. */
abstract sealed class SlackMessage permits SlackThread, SlackReply {

  /**
   * @see <a href="https://stackoverflow.com/a/1547940">Which characters make a URL invalid?</a>
   * @see <a href="https://stackoverflow.com/a/417184/374236">What is the maximum length of a URL in
   *     different browsers?</a>
   */
  private static final String URL_CHARACTER_REGEX = "[A-Za-z\\d-._~:/?#\\[\\]@!$&'()*+,;=%]";

  private static final Pattern SLACK_RAW_URL_PATTERN =
      Pattern.compile("<(?<url>http" + URL_CHARACTER_REGEX + "{10,2000})>");
  private static final Pattern SLACK_TITLED_URL_PATTERN =
      Pattern.compile(
          "<(?<url>http" + URL_CHARACTER_REGEX + "{10,2000})\\|(?<title>[^>]{1,20000})>");

  private static final Pattern BOLD_PATTERN = Pattern.compile("\\*(?<content>[^*]+)\\*");

  private static final Pattern LIST_PATTERN = Pattern.compile("^\\s*•\\s+", Pattern.MULTILINE);
  private static final Pattern SUBLIST_PATTERN = Pattern.compile("^ \\s*◦\\s+", Pattern.MULTILINE);

  public static final String DEFAULT_TS = "9999999999.999999";

  // See https://github.com/slackhq/slack-api-docs/issues/7#issuecomment-67913241.
  private final String timestamp;
  private final String text;
  private final boolean appMessage;

  public SlackMessage(Message message) {
    this.timestamp = message.getTs() == null ? DEFAULT_TS : message.getTs();
    this.text = message.getText() == null ? "" : message.getText();
    this.appMessage = isNotBlank(message.getAppId()) || isNotBlank(message.getBotId());
  }

  public final String timestamp() {
    return timestamp;
  }

  public final String text() {
    return text;
  }

  /**
   * Convert this message {@link #text()} as markdown.
   *
   * @see <a href="https://api.slack.com/reference/surfaces/formatting">Formatting text for app
   *     surfaces</a>
   * @see <a href="https://www.markdownguide.org/tools/slack/">Slack Markdown Reference | Markdown
   *     Guide</a>
   */
  public final String asMarkdown() {
    String markdown = text;

    markdown = SLACK_RAW_URL_PATTERN.matcher(markdown).replaceAll("[${url}](${url})");
    markdown = SLACK_TITLED_URL_PATTERN.matcher(markdown).replaceAll("[${title}](${url})");
    markdown = BOLD_PATTERN.matcher(markdown).replaceAll("**${content}**");
    markdown = LIST_PATTERN.matcher(markdown).replaceAll("- ");
    markdown = SUBLIST_PATTERN.matcher(markdown).replaceAll("  - ");
    markdown = markdown.replace("&gt;", ">");
    markdown = markdown.replace("&lt;", "<");

    return markdown;
  }

  public final boolean isAppMessage() {
    return appMessage;
  }

  public final boolean isUserMessage() {
    return !isAppMessage();
  }
}
