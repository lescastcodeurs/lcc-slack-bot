[![Build](https://github.com/lescastcodeurs/lcc-slack-bot/workflows/Build/badge.svg)](https://github.com/lescastcodeurs/lcc-slack-bot/actions)
[![CodeQL](https://github.com/lescastcodeurs/lcc-slack-bot/workflows/CodeQL/badge.svg)](https://github.com/lescastcodeurs/lcc-slack-bot/actions)

# lcc-slack-bot - _Les Cast Codeurs podcast_ bot

A slack bot that automates show notes creation for _Les Cast Codeurs podcast_.

## Use it !

This bot is using the [Socket Mode](https://api.slack.com/apis/connections/socket) and can be set up
without much hassle.

### Register the bot in your workspace

First you need to create a [Slack app](https://api.slack.com/start) and install it in your workspace :

1. go to [https://api.slack.com/apps](https://api.slack.com/apps),
2. create a new Slack app using this [manifest.yml](/src/main/slack/manifest.yml),
3. optionally set an app icon in _Settings > Basic Information_,
4. create an _app-level token_ with the scope `connections:write` in _Settings > Basic Information_ and save it for
   later (it will be referred as `SLACK_APP_TOKEN`),
5. install the app using the _Install to workspace_ button in _Settings > Basic Information_,
6. save the _Bot User OAuth Token_ for later in _Features > OAuth & Permissions_ (it will be referred
   as `SLACK_BOT_TOKEN`).

### Create customised emojis

Then you need to
[add customised emojis to your workspace](https://slack.com/intl/fr-fr/help/articles/206870177-Ajouter-un-%C3%A9moji-personnalis%C3%A9-et-des-alias-dans-votre-espace-de-travail).
Those will allow you to categorize news using reactions.

The following emojis must be added (suggested emojis can also be found in [this directory](/emojis)) :

| Category                     | Name         | Suggested image                                      |
|------------------------------|--------------|------------------------------------------------------|
| Langages                     | lcc_lang     | https://openmoji.org/library/emoji-E1C1/             |
| Librairies                   | lcc_lib      | https://openmoji.org/library/emoji-1F4DA/            |
| Infrastructure               | lcc_infra    | https://openmoji.org/library/emoji-1F3E3/            |
| Cloud                        | lcc_cloud    | https://openmoji.org/library/emoji-1F32C/            |
| Web                          | lcc_web      | https://openmoji.org/library/emoji-1F310/            |
| Data                         | lcc_data     | https://openmoji.org/library/emoji-1F4BD/            |
| Outillage                    | lcc_outil    | https://openmoji.org/library/emoji-1F6E0/            |
| Architecture                 | lcc_archi    | https://openmoji.org/library/emoji-E04A/             |
| Méthodologies                | lcc_methodo  | https://openmoji.org/library/emoji-1F9D1-200D-1F373/ |
| Sécurité                     | lcc_secu     | https://openmoji.org/library/emoji-1F510/            |
| Loi, société et organisation | lcc_loi      | https://openmoji.org/library/emoji-2696/             |
| Outils de l’épisode          | lcc_outil_ep | https://openmoji.org/library/emoji-1F984/            |
| Rubrique débutant            | lcc_debutant | https://openmoji.org/library/emoji-1F423/            |
| Conférences                  | lcc_conf     | https://openmoji.org/library/emoji-1F3A4/            |
| Messages exclus              | lcc_exclude  | https://openmoji.org/library/emoji-274C/             |
| Messages inclus              | lcc_include  | https://openmoji.org/library/emoji-2714/             |

Note also that aliases does not work because the name returned by the Slack API is not the name of the alias (it is the
name of the aliased emoji).

### Create the GitHub token

Then you need to create a
[GitHub personal access token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)
and a _publication_ repository (a repository where the show notes will be published) :

1. go to [https://github.com/settings/tokens/new](https://github.com/settings/tokens/new),
2. create a new personal access token with the scope `repo` and save it for later (it will be referred
   as `GITHUB_TOKEN`),
3. create, if needed, a new _publication_ repository and keep its coordinates (it will be referred
   as `GITHUB_REPOSITORY`).

### Deploy the `lcc-slack-bot`

> **Warning**
> This way of deploying `lcc-slack-bot` is fragile and will soon be modified. The ultimate goal is to deploy the bot on
> Google Cloud.

First install Java 17 on the target server and create the dedicated user that will run the bot (every instruction
below must be executed with this user from its home directory).

Then download the latest `lcc-slack-bot` jar from
the [`com.lescastcodeurs.lcc-slack-bot` Maven repository](https://github.com/orgs/lescastcodeurs/packages?repo_name=lcc-slack-bot)
and copy it on your server.

You also need to create the launch script using the following template (do not forget to make the script executable) :

```shell
#!/usr/bin/env bash

export GITHUB_TOKEN='ghp_xxx'
export GITHUB_REPOSITORY='lescastcodeurs/staging.lescastcodeurs.com'
export SLACK_BOT_TOKEN='xoxb-xxx'
export SLACK_APP_TOKEN='xapp-xxx'
export JAR=$(find . -name 'lcc-slack-bot-*-runner.jar' | sort | tail -n 1)

java -jar "$JAR" | tee -a 'lcc-slack-bot.log'
```

Finally, you can start the bot [in a screen](https://linux.die.net/man/1/screen) using the following command :

```shell
screen -S lcc-slack-bot -d -RR ./lcc-slack-bot.sh

> __  ____  __  _____   ___  __ ____  ______
>  --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
>  -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
> --\___\_\____/_/ |_/_/|_/_/|_|\____/___/
> 2022-09-04 21:33:28,921 INFO  [io.quarkus] (main) lcc-slack-bot 1.4.0 on JVM (powered by Quarkus 2.12.0.Final) started in 4.069s.
> 2022-09-04 21:33:29,118 INFO  [io.quarkus] (main) Profile prod activated.
> 2022-09-04 21:33:29,119 INFO  [io.quarkus] (main) Installed features: [cdi, qute, smallrye-context-propagation, vertx]
> 2022-09-04 21:33:33,846 INFO  [com.sla.api.soc.SocketModeClient] (Grizzly(2)) New session is open (session id: 123e4567-e89b-12d3-a456-426614174000)
```

Once the bot is started, you can verify it is working using the following message : `@lcc Are you there !?`.
