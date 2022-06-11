[![Build](https://github.com/lescastcodeurs/lcc-slack-bot/workflows/Build/badge.svg)](https://github.com/lescastcodeurs/lcc-slack-bot/actions)
[![CodeQL](https://github.com/lescastcodeurs/lcc-slack-bot/workflows/CodeQL/badge.svg)](https://github.com/lescastcodeurs/lcc-slack-bot/actions)

# lcc-slack-bot - _Les Cast Codeurs podcast_ bot

A slack bot that automates show notes creation for _Les Cast Codeurs podcast_.

## Use it !

This bot is using the [Socket Mode](https://api.slack.com/apis/connections/socket) and can be set up
without much hassle.

First you need to create a [Slack app](https://api.slack.com/start) and install it in your workspace :

1. go to [https://api.slack.com/apps](https://api.slack.com/apps),
2. create a new Slack app using this [manifest.yml](/src/main/slack/manifest.yml),
3. optionally set an app icon in _Settings > Basic Information_,
4. create an _App-Level Tokens_ with the scope `connections:write` in _Settings > Basic Information_ and save it for
   later (it will be referred as `SLACK_APP_TOKEN`),
5. install the app using the _Install to worspace_ button in _Settings > Basic Information_,
6. save the _Bot User OAuth Token_ for later in _Features > OAuth & Permissions_ (it will be referred
   as `SLACK_BOT_TOKEN`).

You can then start the bot using the following commands (from the project directory) :

```shell
quarkus build

export SLACK_APP_TOKEN='xapp-XXX'
export SLACK_BOT_TOKEN='xoxb-XXX'
java -jar build/quarkus-app/quarkus-run.jar
```

Once the bot is started, you can mention it in your messages : `@lcc Help me please !`.
