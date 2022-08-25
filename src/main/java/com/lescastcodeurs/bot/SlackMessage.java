package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.StringUtils.isNotBlank;

import com.slack.api.model.Message;
import java.util.List;
import java.util.regex.Pattern;

/** Hold required information from a Slack message. */
public final class SlackMessage {

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
  private final List<String> replies;
  private final boolean appMessage;

  public SlackMessage(String timestamp, String text, List<String> replies, boolean appMessage) {
    this.timestamp = timestamp == null ? DEFAULT_TS : timestamp;
    this.text = text == null ? "" : text;
    this.replies = replies == null ? List.of() : List.copyOf(replies);
    this.appMessage = appMessage;
  }

  public SlackMessage(Message message, List<String> replies) {
    this(
        message.getTs(),
        message.getText(),
        replies,
        isNotBlank(message.getAppId()) || isNotBlank(message.getBotId()));
  }

  public String timestamp() {
    return timestamp;
  }

  public String text() {
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
  public String asMarkdown() {
    return convertToMarkdown(text);
  }

  public List<String> replies() {
    return replies;
  }

  public List<String> repliesAsMarkdown() {
    return replies.stream().map(this::convertToMarkdown).toList();
  }

  public boolean isAppMessage() {
    return appMessage;
  }

  public boolean isUserMessage() {
    return !isAppMessage();
  }

  private String convertToMarkdown(String text) {
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
}
