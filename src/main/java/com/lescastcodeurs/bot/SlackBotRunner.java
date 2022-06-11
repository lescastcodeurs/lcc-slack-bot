package com.lescastcodeurs.bot;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.socket_mode.SocketModeApp;
import com.slack.api.model.event.AppMentionEvent;
import io.quarkus.runtime.QuarkusApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Implements the bot logic.
 */
public class SlackBotRunner implements QuarkusApplication {

  public static final String SLACK_BOT_TOKEN_ENV_NAME = "SLACK_BOT_TOKEN";
  public static final String SLACK_APP_TOKEN_ENV_NAME = "SLACK_APP_TOKEN";
  private static final Logger LOG = LoggerFactory.getLogger(SlackBotRunner.class);

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
    String botToken = System.getenv(SLACK_BOT_TOKEN_ENV_NAME);
    AppConfig appConfig = AppConfig.builder().singleTeamBotToken(botToken).build();

    App app = new App(appConfig);
    app.event(AppMentionEvent.class, (req, ctx) -> {
      LOG.debug("Received : {}", req);
      ctx.say("Hey there !");
      return ctx.ack();
    });

    String appToken = System.getenv(SLACK_APP_TOKEN_ENV_NAME);
    return new SocketModeApp(appToken, app);
  }
}
