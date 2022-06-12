package com.lescastcodeurs.bot;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.model.event.AppMentionEvent;
import io.quarkus.runtime.QuarkusApplication;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Listen to Slack {@link AppMentionEvent}s and respond to the given commands.
 * <p>
 * Note that <a href="https://api.slack.com/legacy/interactive-messages#responding_right_away">Slack needs an HTTP 200
 * OK response within 3 seconds</a>. Commands that may take more time to be processed must be handled asynchronously and
 * in the context a new Slack message.
 */
public class SlackMentionListener implements QuarkusApplication {

  private static final Logger LOG = LoggerFactory.getLogger(SlackMentionListener.class);

  private final String appToken;
  private final String botToken;

  public SlackMentionListener(@ConfigProperty(name = "slack.app.token") String appToken,
                              @ConfigProperty(name = "slack.bot.token") String botToken) {
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
    app.event(AppMentionEvent.class, (req, ctx) -> {
      LOG.debug("Received : {}", req);

      SlackBotAction command = SlackBotAction.guess(req.getEvent().getText());
      ctx.say(command.response());

      return ctx.ack();
    });

    return new SocketModeApp(appToken, app);
  }
}
