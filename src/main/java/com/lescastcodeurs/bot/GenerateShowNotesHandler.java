package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.Constants.GENERATE_SHOW_NOTES_ADDRESS;
import static com.lescastcodeurs.bot.Constants.GITHUB_REPOSITORY;
import static com.lescastcodeurs.bot.Constants.LCC_RECORD_DATE_CRITERION;
import static com.lescastcodeurs.bot.internal.StringUtils.asFilename;
import static java.util.Objects.requireNonNull;

import com.lescastcodeurs.bot.conferences.ConferencesClient;
import com.lescastcodeurs.bot.github.GitHubClient;
import com.lescastcodeurs.bot.slack.SlackClient;
import com.lescastcodeurs.bot.slack.SlackMentionEvent;
import com.lescastcodeurs.bot.slack.SlackThread;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/** Handles {@link SlackBotAction#GENERATE_SHOW_NOTES} commands. */
@ApplicationScoped
public final class GenerateShowNotesHandler extends LongTaskHandlerSupport<Void> {

  private final Template notes;
  private final SlackClient slackClient;
  private final GitHubClient gitHubClient;
  private final ConferencesClient conferencesClient;
  private final String gitHubRepository;
  private final String recordDateCriterion;

  @Inject
  public GenerateShowNotesHandler(
      SlackClient slackClient,
      GitHubClient gitHubClient,
      ConferencesClient conferencesClient,
      @ConfigProperty(name = GITHUB_REPOSITORY) String gitHubRepository,
      @ConfigProperty(name = LCC_RECORD_DATE_CRITERION) String recordDateCriterion,
      @Location("show-notes.md") Template notes) {
    this.slackClient = requireNonNull(slackClient);
    this.gitHubClient = requireNonNull(gitHubClient);
    this.conferencesClient = requireNonNull(conferencesClient);
    this.gitHubRepository = requireNonNull(gitHubRepository);
    this.recordDateCriterion = requireNonNull(recordDateCriterion);
    this.notes = requireNonNull(notes);
  }

  @Override
  String description() {
    return "Generation of show notes";
  }

  @ConsumeEvent(value = GENERATE_SHOW_NOTES_ADDRESS, blocking = true)
  public void consume(SlackMentionEvent event) throws InterruptedException {
    execute(
        event,
        () -> {
          String channelName = slackClient.name(event.channel());
          List<SlackThread> threads = slackClient.history(event.channel(), true);
          MarkdownSerializable conferences = conferencesClient.getConferences();
          LocalDateTime recordDate = retrieveRecordDate(threads);

          String filename = asFilename(channelName, "md");
          String content =
              notes.render(new ShowNotes(channelName, threads, conferences, recordDate));
          String showNoteUrl = gitHubClient.createOrUpdateFile(gitHubRepository, filename, content);

          slackClient.chatPostMessage(
              event.channel(),
              event.replyTs(),
              "C'est fait, les show notes sont disponibles sur <%s>.".formatted(showNoteUrl));
          return null;
        },
        e ->
            slackClient.chatPostMessage(
                event.channel(),
                event.replyTs(),
                "Désolé, une erreur est survenue : %s - %s. Pour plus d'informations voir les logs du bot. Pensez à réessayer votre commande au cas où, notamment en cas de timeout."
                    .formatted(e.getClass().getSimpleName(), e.getMessage())));
  }

  private LocalDateTime retrieveRecordDate(List<SlackThread> threads) {
    LocalDateTime recordDate = LocalDateTime.now();

    for (SlackThread thread : threads) {
      if (thread.text().contains(recordDateCriterion)) {
        recordDate = thread.dateTime(); // last matching thread wins
      }
    }

    log.info("The following record date found will be used: {}", recordDate);
    return recordDate;
  }
}
