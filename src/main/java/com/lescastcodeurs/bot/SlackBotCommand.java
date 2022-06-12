package com.lescastcodeurs.bot;

import java.util.regex.Pattern;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.joining;

public enum SlackBotCommand {

  ARE_YOU_THERE(1, ".*are you there.*", "are you there", "Yep, I'm all ears."),

  GENERATE_SHOW_NOTES(2, ".*generate show ?notes?.*", "generate show notes", "OK, I'm on it !"),

  HELP(3, ".*help.*", "help", null) {
    @Override
    public String response() {
      String commands = stream(SlackBotCommand.values())
        .map(SlackBotCommand::guessPattern)
        .map(Object::toString)
        .collect(joining(" | "));

      return "Here are the command I can respond to : " + commands + ".";
    }
  },

  UNKNOWN(999, ".*", null, null) {
    @Override
    public String response() {
      return "Sorry, I don't understand. " + HELP.response();
    }
  };

  /**
   * Determine the order in which the commands will be evaluated during the "guessing process".
   */
  private final int guessOrder;

  /**
   * A pattern used to determine the command associated to a message.
   */
  private final Pattern guessPattern;

  /**
   * A sample command.
   */
  private final String sampleCommand;

  /**
   * The command response message.
   */
  private final String response;

  SlackBotCommand(int guessOrder, String guessRegex, String sampleCommand, String response) {
    this.guessOrder = guessOrder;
    this.guessPattern = Pattern.compile(guessRegex, Pattern.CASE_INSENSITIVE);
    this.sampleCommand = sampleCommand;
    this.response = response;
  }

  public static SlackBotCommand guess(String request) {
    return stream(values())
      .sorted(comparingInt(SlackBotCommand::guessOrder))
      .filter(c -> c.canReplyTo(request))
      .findFirst().orElse(UNKNOWN);
  }

  private boolean canReplyTo(String request) {
    if (request == null) {
      return false;
    }

    return guessPattern.matcher(request).matches();
  }

  public int guessOrder() {
    return guessOrder;
  }

  public Pattern guessPattern() {
    return guessPattern;
  }

  public String sampleCommand() {
    return sampleCommand;
  }

  public String response() {
    return response;
  }
}
