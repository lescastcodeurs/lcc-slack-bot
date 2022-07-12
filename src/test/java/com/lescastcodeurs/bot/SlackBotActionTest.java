package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.SlackBotAction.UNKNOWN;
import static com.lescastcodeurs.bot.SlackBotAction.guess;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class SlackBotActionTest {

  @ParameterizedTest
  @EnumSource(SlackBotAction.class)
  void guessWithLowerCase(SlackBotAction action) {
    for (String command :
        Stream.of(action.keywords(), action.usages()).flatMap(Collection::stream).toList()) {
      SlackBotAction guessed = guess(command.toLowerCase(Locale.ROOT));

      assertEquals(action, guessed);
    }
  }

  @ParameterizedTest
  @EnumSource(SlackBotAction.class)
  void guessWithUpperCase(SlackBotAction action) {
    for (String command :
        Stream.of(action.keywords(), action.usages()).flatMap(Collection::stream).toList()) {
      SlackBotAction guessed = guess(command.toUpperCase(Locale.ROOT));

      assertEquals(action, guessed);
    }
  }

  @ParameterizedTest
  @EnumSource(SlackBotAction.class)
  void guessWithPunctuation(SlackBotAction action) {
    for (String command :
        Stream.of(action.keywords(), action.usages()).flatMap(Collection::stream).toList()) {
      SlackBotAction guessed = guess("%s !!!".formatted(command));

      assertEquals(action, guessed);
    }
  }

  @ParameterizedTest
  @EnumSource(SlackBotAction.class)
  void responseNeverFails(SlackBotAction action) {
    String response = action.response();

    assertNotNull(response);
    assertFalse(response.isBlank());
  }

  @ParameterizedTest
  @EnumSource(SlackBotAction.class)
  void helpNeverFailsExceptForUnknown(SlackBotAction action) {
    if (action != UNKNOWN) {
      String help = action.help();

      assertNotNull(help);
      assertFalse(help.isBlank());
    }
  }
}
