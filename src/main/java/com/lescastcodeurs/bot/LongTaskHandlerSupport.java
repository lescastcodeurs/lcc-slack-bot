package com.lescastcodeurs.bot;

import static org.slf4j.LoggerFactory.getLogger;

import com.lescastcodeurs.bot.internal.Stopwatch;
import com.lescastcodeurs.bot.slack.SlackMentionEvent;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import org.slf4j.Logger;

abstract class LongTaskHandlerSupport<R> {

  protected final Logger log = getLogger(getClass());

  abstract String description();

  Optional<R> execute(
      SlackMentionEvent event, Callable<R> task, Consumer<Exception> exceptionHandler)
      throws InterruptedException {
    Stopwatch watch = new Stopwatch();
    String channel = event.channel();
    String ts = event.ts();
    String desc = description();

    try {
      log.info("starting {} for mention {} in channel {}", desc, ts, channel);
      R result = task.call();
      log.info("{} succeeded for for message {} in channel {}, took {}", desc, ts, channel, watch);
      return Optional.ofNullable(result);
    } catch (InterruptedException e) {
      throw e;
    } catch (Exception e) {
      log.error("{} failed for for message {} in channel {}, took {}", desc, ts, channel, watch, e);
      exceptionHandler.accept(e);
      return Optional.empty();
    }
  }
}
