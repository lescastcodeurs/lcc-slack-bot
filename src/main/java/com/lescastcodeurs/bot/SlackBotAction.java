package com.lescastcodeurs.bot;

import java.util.Locale;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public enum SlackBotAction {

  ARE_YOU_THERE("are you there", "Yep, I'm all ears."),

  GENERATE_SHOW_NOTES("generate show notes", "OK, I'm on it !"),

  HELP("help") {
    @Override
    public String response() {
      String commands = stream(SlackBotAction.values())
        .map(SlackBotAction::command)
        .collect(joining(" | "));

      return "Here are the command I can respond to : " + commands + ".";
    }
  },

  UNKNOWN(null) {
    @Override
    public String response() {
      return "Sorry, I don't understand. " + HELP.response();
    }
  };

  private final String command;
  private final String response;

  SlackBotAction(String command, String response) {
    this.command = command == null ? null : command.toLowerCase(Locale.ROOT);
    this.response = response;
  }

  SlackBotAction(String command) {
    this(command, null);
  }

  public static SlackBotAction guess(String request) {
    if(request == null) {
      return UNKNOWN;
    }

    String message = request.trim().toLowerCase(Locale.ROOT);
    for(SlackBotAction candidate : values()) {
      if(candidate != UNKNOWN && message.contains(candidate.command)) {
        return candidate;
      }
    }

    return UNKNOWN;
  }

  public String command() {
    return command;
  }

  public String response() {
    return response;
  }
}
