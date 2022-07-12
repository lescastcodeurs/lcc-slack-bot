package com.lescastcodeurs.bot;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * This class is no different Quarkus automatically generated main class, but has the advantage that
 * you can just launch it directly from the IDE without needing to run a Gradle command.
 */
@QuarkusMain
public class Main {

  public static void main(String... args) {
    Quarkus.run(SlackMentionListener.class, args);
  }
}
