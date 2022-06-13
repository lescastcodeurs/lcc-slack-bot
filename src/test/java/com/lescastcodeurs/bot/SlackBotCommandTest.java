package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.SlackBotCommand.guess;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class SlackBotCommandTest {

  @ParameterizedTest
  @EnumSource(SlackBotCommand.class)
  void guessWithLowerCase(SlackBotCommand action) {
    String request = action.sampleCommand() == null
      ? null
      : action.sampleCommand().toLowerCase(Locale.ROOT);

    SlackBotCommand guessed = guess(request);

    assertEquals(action, guessed);
  }

  @ParameterizedTest
  @EnumSource(SlackBotCommand.class)
  void guessWithUpperCase(SlackBotCommand action) {
    String request = action.sampleCommand() == null
      ? null
      : action.sampleCommand().toUpperCase(Locale.ROOT);

    SlackBotCommand guessed = guess(request);

    assertEquals(action, guessed);
  }

  @ParameterizedTest
  @EnumSource(SlackBotCommand.class)
  void guessWithPunctuation(SlackBotCommand action) {
    SlackBotCommand guessed = guess("%s !!!".formatted(action.sampleCommand()));

    assertEquals(action, guessed);
  }

  @ParameterizedTest
  @EnumSource(SlackBotCommand.class)
  void responseNeverFails(SlackBotCommand action) {
    String response = action.response();

    assertNotNull(response);
    assertFalse(response.isBlank());
  }
}
