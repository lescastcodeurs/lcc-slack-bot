package com.lescastcodeurs.bot;

import com.slack.api.bolt.App;
import com.slack.api.bolt.servlet.SlackAppServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet("/slack/events")
public class Application extends SlackAppServlet {
  private static final long serialVersionUID = 1L;

  public Application() { super(initSlackApp()); }

  public Application(App app) { super(app); }

  private static App initSlackApp() {
    App app = new App();
    app.command("/hello", (req, ctx) -> {
      return ctx.ack("What's up?");
    });
    return app;
  }
}
