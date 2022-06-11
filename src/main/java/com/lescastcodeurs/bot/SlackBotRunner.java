package com.lescastcodeurs.bot;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the bot logic.
 */
public class SlackBotRunner implements QuarkusApplication {

  private static final Logger LOG = LoggerFactory.getLogger(SlackBotRunner.class);

  @Override
  public int run(String... args) {
    LOG.info("Waiting for mentions from Slack...");
    Quarkus.waitForExit();
    return 0;
  }
}
