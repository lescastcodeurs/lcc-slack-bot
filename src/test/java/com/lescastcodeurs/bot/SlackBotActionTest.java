package com.lescastcodeurs.bot;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Locale;

import static com.lescastcodeurs.bot.SlackBotAction.guess;
import static org.junit.jupiter.api.Assertions.*;

class SlackBotActionTest {

  @ParameterizedTest
  @EnumSource(SlackBotAction.class)
  void guessWithLowerCase(SlackBotAction action) {
    String request = action.command() == null ? null : action.command().toLowerCase(Locale.ROOT);

    SlackBotAction guessed = guess(request);

    assertEquals(guessed, action);
  }

  @ParameterizedTest
  @EnumSource(SlackBotAction.class)
  void guessWithUpperCase(SlackBotAction action) {
    String request = action.command() == null ? null : action.command().toUpperCase(Locale.ROOT);

    SlackBotAction guessed = guess(request);

    assertEquals(guessed, action);
  }

  @ParameterizedTest
  @EnumSource(SlackBotAction.class)
  void guessWithPunctuation(SlackBotAction action) {
    SlackBotAction guessed = guess("%s !!!".formatted(action.command()));

    assertEquals(guessed, action);
  }

  @ParameterizedTest
  @EnumSource(SlackBotAction.class)
  void responseNeverFails(SlackBotAction action) {
    String response = action.response();

    assertNotNull(response);
    assertFalse(response.isBlank());
  }

}
