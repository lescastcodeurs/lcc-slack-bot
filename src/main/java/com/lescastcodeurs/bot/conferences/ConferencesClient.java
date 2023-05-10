package com.lescastcodeurs.bot.conferences;

import static com.lescastcodeurs.bot.Constants.CONFERENCES_JSON_URL;
import static com.lescastcodeurs.bot.Constants.CONFERENCES_SELECTION_CRITERIA;
import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

import com.lescastcodeurs.bot.MarkdownSerializable;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

@ApplicationScoped
public class ConferencesClient {

  private final Logger log = getLogger(getClass());

  private final HttpClient client;
  private final String jsonUrl;
  private final List<String> selectionCriteria;

  public ConferencesClient(
      @ConfigProperty(name = CONFERENCES_JSON_URL) String jsonUrl,
      @ConfigProperty(name = CONFERENCES_SELECTION_CRITERIA) List<String> selectionCriteria) {
    this.client =
        HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    this.jsonUrl = requireNonNull(jsonUrl);
    this.selectionCriteria = requireNonNull(selectionCriteria);
  }

  public MarkdownSerializable getConferences() throws InterruptedException {
    MarkdownSerializable result;

    try {
      result = new Conferences(getJson(), selectionCriteria);
      log.info("Conferences list was successfully retrieved from {}", jsonUrl);
    } catch (ConferencesException e) {
      log.warn("There was an error while retrieving conferences list from {}", jsonUrl, e);
      return new NoConferenceMarkdown();
    }

    return result;
  }

  private String getJson() throws InterruptedException {
    HttpResponse<String> response =
        send(HttpRequest.newBuilder(URI.create(jsonUrl)).GET().build(), 200);

    return response.body();
  }

  private HttpResponse<String> send(HttpRequest request, Integer... expectedResponseCodes)
      throws InterruptedException {
    try {
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

      int status = response.statusCode();
      Set<Integer> expectedStatuses = Set.of(expectedResponseCodes);
      if (expectedStatuses.contains(status)) {
        return response;
      } else {
        throw new ConferencesException(response);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
