package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.Constants.GENERATE_SHOW_NOTES_ADDRESS;

import com.slack.api.model.event.AppMentionEvent;
import io.quarkus.vertx.ConsumeEvent;
import javax.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles {@link SlackBotCommand#GENERATE_SHOW_NOTES} commands.
 */
@ApplicationScoped
public final class GenerateShowNotesHandler {

  private static final Logger LOG = LoggerFactory.getLogger(
    GenerateShowNotesHandler.class
  );

  @ConsumeEvent(GENERATE_SHOW_NOTES_ADDRESS)
  public void consume(AppMentionEvent event) {
    LOG.info("Show notes generated for channel {}", event.getChannel());
  }
}
