package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.Constants.SLACK_APP_TOKEN;
import static com.lescastcodeurs.bot.Constants.SLACK_BOT_TOKEN;
import static java.util.Objects.requireNonNull;

import com.lescastcodeurs.bot.internal.Stopwatch;
import com.lescastcodeurs.bot.slack.SlackClient;
import com.lescastcodeurs.bot.slack.SlackMentionEvent;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.jakarta_socket_mode.SocketModeApp;
import com.slack.api.model.event.AppMentionEvent;
import io.quarkus.runtime.QuarkusApplication;
import io.vertx.core.eventbus.EventBus;
import java.io.IOException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listen to Slack {@link AppMentionEvent}s and respond to the given commands.
 *
 * <p>Note that <a
 * href="https://api.slack.com/legacy/interactive-messages#responding_right_away">Slack needs an
 * HTTP 200 OK response within 3 seconds</a>. Commands that may take more time to be processed must
 * be handled asynchronously.
 */
public class SlackMentionListener implements QuarkusApplication {

  private static final Logger LOG = LoggerFactory.getLogger(SlackMentionListener.class);

  private final SlackClient client;
  private final EventBus bus;
  private final String appToken;
  private final String botToken;

  public SlackMentionListener(
      SlackClient client,
      EventBus bus,
      @ConfigProperty(name = SLACK_APP_TOKEN) String appToken,
      @ConfigProperty(name = SLACK_BOT_TOKEN) String botToken) {
    this.client = requireNonNull(client);
    this.bus = requireNonNull(bus);
    this.appToken = requireNonNull(appToken);
    this.botToken = requireNonNull(botToken);
  }

  @Override
  public int run(String... args) {
    try {
      createApp().start();
      return 0;
    } catch (Exception e) {
      LOG.error("Could not create or start app");
      return 1;
    }
  }

  private SocketModeApp createApp() throws IOException {
    AppConfig appConfig = AppConfig.builder().singleTeamBotToken(botToken).build();

    App app = new App(appConfig);
    app.event(
        AppMentionEvent.class,
        (req, ctx) -> {
          Stopwatch stopwatch = new Stopwatch();
          SlackMentionEvent event = new SlackMentionEvent(req.getEvent());
          SlackBotAction command = SlackBotAction.guess(event.text());
          String channel = event.channel();

          try {
            LOG.info("Processing command {} (ts={}) in channel {}", command, channel, event.ts());
            client.chatPostMessage(channel, event.replyTs(), command.response());
            command.handlerAddress().ifPresent(address -> bus.publish(address, event));
            var response = ctx.ack();
            LOG.info(
                "Command {} (ts={}) in channel {} processed, took {}",
                command,
                channel,
                event.ts(),
                stopwatch);
            return response;
          } catch (RuntimeException e) {
            LOG.error(
                "Command {} (ts={}) in channel {} failed, took {}",
                command,
                channel,
                event.ts(),
                stopwatch);
            throw e;
          }
        });

    return new SocketModeApp(appToken, app);
  }
}
