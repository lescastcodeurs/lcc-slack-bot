package com.lescastcodeurs.bot.github;

import static com.lescastcodeurs.bot.Constants.GITHUB_TOKEN;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GitHubClient {

  private static final String GITHUB_URL = "https://github.com";
  private static final String GITHUB_API_URL = "https://api.github.com";

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final String token;

  private final Base64.Encoder encoder;
  private final HttpClient client;

  public GitHubClient(@ConfigProperty(name = GITHUB_TOKEN) String token) {
    this.token = token;
    this.client =
        HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    this.encoder = Base64.getEncoder();
  }

  /**
   * Create or update a file in the given repository under the given filename.
   *
   * @see <a
   *     href="https://docs.github.com/en/rest/repos/contents#create-or-update-file-contents">Create
   *     or update file contents</a>
   */
  public String createOrUpdateFile(String repository, String filename, String content)
      throws InterruptedException {
    Map<String, Object> commit = new HashMap<>();
    commit.put("message", "publish show notes");
    commit.put("committer", Map.of("name", "@lcc", "email", "commentaire@lescastcodeurs.com"));
    commit.put("content", encoder.encodeToString(content.getBytes(UTF_8)));
    getSha(repository, filename).ifPresent(s -> commit.put("sha", s));

    String body;
    try {
      body = MAPPER.writeValueAsString(commit);
    } catch (JsonProcessingException e) {
      throw new GitHubApiException("Cannot generate JSON", e);
    }

    send(
        HttpRequest.newBuilder(URI.create(gitHubApiUrl(repository, filename)))
            .header("Accept", "application/vnd.github.v3+json")
            .header("Authorization", "token " + token)
            .PUT(HttpRequest.BodyPublishers.ofString(body))
            .build(),
        200,
        201);

    return gitHubUrl(repository, filename);
  }

  public String getContent(String repository, String filename) throws InterruptedException {
    HttpResponse<String> response =
        send(
            HttpRequest.newBuilder(URI.create(gitHubApiUrl(repository, filename)))
                .header("Accept", "application/vnd.github.v3.raw")
                .header("Authorization", "token " + token)
                .GET()
                .build(),
            200);

    return response.body();
  }

  private Optional<String> getSha(String repository, String filename) throws InterruptedException {
    String sha = null;

    HttpResponse<String> response =
        send(
            HttpRequest.newBuilder(URI.create(gitHubApiUrl(repository, filename)))
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", "token " + token)
                .GET()
                .build(),
            200,
            404);

    if (response.statusCode() == 200) {
      try {
        Map<String, Object> data =
            MAPPER.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
        Object value = data.get("sha");
        sha = value == null ? null : value.toString();
      } catch (JsonProcessingException e) {
        throw new GitHubApiException("Cannot parse JSON", e);
      }
    }

    return Optional.ofNullable(sha);
  }

  private String gitHubApiUrl(String repository, String filename) {
    return "%s/repos/%s/contents/%s".formatted(GITHUB_API_URL, repository, filename);
  }

  private String gitHubUrl(String repository, String filename) {
    return "%s/%s/blob/main/%s".formatted(GITHUB_URL, repository, filename);
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
        throw new GitHubApiException(response);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
