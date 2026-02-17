# Copilot Instructions for lcc-slack-bot

A Slack bot for automating show notes creation for _Les Cast Codeurs_ podcast. Built with Quarkus, the bot collects messages from Slack channels and publishes formatted show notes to GitHub.

## Build, Test, and Lint

### Prerequisites

- Java 17 (Eclipse Temurin)
- Gradle (see `.tool-versions` for exact version)
- Quarkus CLI (optional but recommended)

Use asdf to install dependencies: `asdf install`

### Build Commands

```bash
# Development mode with live reload
quarkus dev

# Production build
quarkus build

# Or with Gradle
gradle build

# Run the built application
java -jar build/quarkus-app/quarkus-run.jar
```

### Testing

```bash
# Run all tests
gradle test

# Run a single test class
gradle test --tests com.lescastcodeurs.bot.ShowNotesTest

# Run a single test method
gradle test --tests com.lescastcodeurs.bot.ShowNotesTest.testMethodName
```

### Code Formatting

Code is automatically formatted during build using google-java-format (Google Java Style).

```bash
# Manually format code
gradle spotlessApply

# Check formatting (without auto-fixing)
gradle spotlessCheck
```

**Important**: Code is auto-formatted on local builds but only checked on CI. Install the google-java-format IDE plugin if available.

## Architecture Overview

### High-Level Flow

1. **Slack Integration**: Bot listens for mentions using Socket Mode (no webhook required)
2. **Command Processing**: Commands are parsed via `SlackBotAction` enum which uses keyword matching
3. **Show Notes Generation**: Slack threads are converted to show notes using Qute templates
4. **GitHub Publishing**: Formatted markdown is committed to a GitHub repository

### Key Components

- **`SlackBotAction` (enum)**: Defines all bot commands with keywords, responses, and handlers. Commands are matched by normalizing input and checking keywords in `guessOrder` priority.
- **`ShowNotes`**: Main domain model that aggregates `ShowNote` objects by category. Episode number is extracted from channel name using regex.
- **`ShowNoteCategory` (enum)**: Categories mapped to custom Slack emoji reactions (e.g., `lcc_lang`, `lcc_lib`). Uses `INCLUDE`/`EXCLUDE` for inclusion/exclusion logic.
- **`SlackThread`**: Represents a Slack message thread with reactions and replies. First message is the show note, replies are comments.
- **`GitHubClient`**: Publishes show notes to GitHub repository using GitHub API.
- **Qute Templates** (`src/main/resources/templates/`): Generate markdown from show notes data.

### Package Structure

```
com.lescastcodeurs.bot/
├── github/          # GitHub API integration
├── slack/           # Slack API integration and message handling
├── conferences/     # Conference data retrieval
└── internal/        # Utility classes (StringUtils, etc.)
```

### Show Note Processing Logic

- Threads are considered show notes if:
  - First message is from a user (not a bot)
  - Has a categorization reaction OR contains a link with no mentions
  - Not explicitly excluded with `:lcc_exclude:` reaction
- Replies are included if:
  - Written by a user (not a bot)
  - No `:lcc_exclude:` reaction
  - Has `:lcc_include:` reaction OR contains no mentions
- Ordering: Custom reactions `:lcc_1:` through `:lcc_9:` control sort order, otherwise chronological

## Key Conventions

### Enum-Driven Command Pattern

Commands are defined as enum values in `SlackBotAction`. Each action has:
- `keywords`: List of normalized keywords to match
- `response`: Default response message
- `handlerAddress`: Optional Vert.x event bus address for async processing
- `canReplyTo()`: Override for custom matching logic

New commands: Add a new enum value with appropriate `guessOrder` (determines evaluation priority).

### Reaction-Based Categorization

Categories use custom Slack emojis (all prefixed `lcc_`):
- Category reactions: `lcc_lang`, `lcc_lib`, `lcc_infra`, etc.
- Special reactions: `lcc_include`, `lcc_exclude` (inclusion/exclusion)
- Ordering reactions: `lcc_1` through `lcc_9`

The last reaction added wins (except `:lcc_exclude:` which is always prioritized).

### Configuration via Environment Variables

Runtime configuration uses environment variables (not `application.properties`):
- `SLACK_BOT_TOKEN`, `SLACK_APP_TOKEN`: Slack credentials
- `GITHUB_TOKEN`, `GITHUB_REPOSITORY`: GitHub publishing
- `CONFERENCES_JSON_URL`, `CONFERENCES_SELECTION_CRITERIA`: Conference list filtering
- `LCC_RECORD_DATE_CRITERION`: String to identify record date messages

Default values in `application.properties` are for local development only.

### Quarkus Dependency Injection

Uses Quarkus CDI (`@ApplicationScoped`, `@Inject`). Avoid manual instantiation of services; let the container manage lifecycle.

### Vert.x Event Bus for Async Processing

Commands requiring heavy processing (e.g., `GENERATE_SHOW_NOTES`) publish events to Vert.x event bus addresses. Handlers are registered with `@ConsumeEvent`.

## Publishing

Releases are triggered by creating a GitHub release with tag `x.y.z`. The `publish.yml` workflow publishes to GitHub Packages (Maven repository). Update `CHANGELOG.md` before creating the release.
