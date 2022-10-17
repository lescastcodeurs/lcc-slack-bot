package com.lescastcodeurs.bot.slack;

import static com.lescastcodeurs.bot.internal.StringUtils.isNotBlank;

import com.lescastcodeurs.bot.internal.SlackUtils;
import com.slack.api.model.Message;
import com.slack.api.model.Reaction;
import java.time.LocalDateTime;
import java.util.List;
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
  private static final Pattern MENTION_PATTERN = Pattern.compile(".*<[@!][A-Za-z0-9]+>.*");

  public static final String DEFAULT_TS = "9999999999.999999";
  public static final LocalDateTime DEFAULT_DATE_TIME = LocalDateTime.MAX;

  // See https://github.com/slackhq/slack-api-docs/issues/7#issuecomment-67913241.
  private final String timestamp;
  private final LocalDateTime dateTime;
  private final String text;
  private final List<String> reactions;
  private final boolean appMessage;

  SlackMessage(Message message) {
    this.timestamp = message.getTs() == null ? DEFAULT_TS : message.getTs();
    this.dateTime = SlackUtils.parseTimestamp(message.getTs(), DEFAULT_DATE_TIME);
    this.text = message.getText() == null ? "" : message.getText();
    this.reactions =
        message.getReactions() == null
            ? List.of()
            : message.getReactions().stream().map(Reaction::getName).toList();
    this.appMessage = isNotBlank(message.getAppId()) || isNotBlank(message.getBotId());
  }

  public final String timestamp() {
    return timestamp;
  }

  public final LocalDateTime dateTime() {
    return dateTime;
  }

  public final String text() {
    return text;
  }

  public final List<String> reactions() {
    return reactions;
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

  public boolean hasLink() {
    return text.contains("<http");
  }

  public boolean hasMention() {
    return MENTION_PATTERN.matcher(text).matches();
  }
}
