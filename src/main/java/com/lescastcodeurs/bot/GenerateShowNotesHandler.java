package com.lescastcodeurs.bot;

import com.slack.api.model.event.AppMentionEvent;
import io.quarkus.vertx.ConsumeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 * Handles {@link SlackBotCommand#GENERATE_SHOW_NOTES} commands.
 */
@ApplicationScoped
public final class GenerateShowNotesHandler {
  private static final Logger LOG = LoggerFactory.getLogger(GenerateShowNotesHandler.class);

  @ConsumeEvent(HandlerAddresses.GENERATE_SHOW_NOTES)
  public void consume(AppMentionEvent event) {
    LOG.info("Show notes generated for channel {}", event.getChannel());
  }
}
