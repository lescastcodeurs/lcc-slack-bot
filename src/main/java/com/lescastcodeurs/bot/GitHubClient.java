package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.Constants.GITHUB_REPOSITORY;
import static com.lescastcodeurs.bot.Constants.GITHUB_TOKEN;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.json.Json.createObjectBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GitHubClient {

  private static final String GITHUB_URL = "https://github.com";
  private static final String GITHUB_API_URL = "https://api.github.com";

  private final String token;
  private final String repository;

  private final Base64.Encoder encoder;
  private final HttpClient client;

  public GitHubClient(
    @ConfigProperty(name = GITHUB_TOKEN) String token,
    @ConfigProperty(name = GITHUB_REPOSITORY) String repository
  ) {
    this.token = token;
    this.repository = repository;
    this.client =
      HttpClient
        .newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .connectTimeout(Duration.ofSeconds(10))
        .build();
    this.encoder = Base64.getEncoder();
  }

  /**
   * Create or update a file in the configured {@link #repository} under the given filename.
   *
   * @see <a href="https://docs.github.com/en/rest/repos/contents#create-or-update-file-contents">Create or update file contents</a>
   */
  public String createOrUpdateFile(String filename, String content)
    throws InterruptedException {
    JsonObjectBuilder commit = createObjectBuilder()
      .add("message", "publish show notes")
      .add(
        "committer",
        Json
          .createObjectBuilder()
          .add("name", "@lcc")
          .add("email", "commentaire@lescastcodeurs.com")
      )
      .add("content", encoder.encodeToString(content.getBytes(UTF_8)));

    Optional<String> sha = getSha(filename);
    sha.ifPresent(s -> commit.add("sha", s));

    send(
      HttpRequest
        .newBuilder(URI.create(gitHubApiUrl(filename)))
        .header("Accept", "application/vnd.github.v3+json")
        .header("Authorization", "token " + token)
        .PUT(HttpRequest.BodyPublishers.ofString(commit.build().toString()))
        .build(),
      200,
      201
    );

    return gitHubUrl(filename);
  }

  private Optional<String> getSha(String filename) throws InterruptedException {
    String sha = null;

    HttpResponse<String> response = send(
      HttpRequest
        .newBuilder(URI.create(gitHubApiUrl(filename)))
        .header("Accept", "application/vnd.github.v3+json")
        .header("Authorization", "token " + token)
        .GET()
        .build(),
      200,
      404
    );

    if (response.statusCode() == 200) {
      try (
        JsonReader reader = Json.createReader(new StringReader(response.body()))
      ) {
        JsonObject body = reader.readObject();
        sha = body.getString("sha");
      }
    }

    return Optional.ofNullable(sha);
  }

  private String gitHubApiUrl(String filename) {
    return "%s/repos/%s/contents/%s".formatted(
        GITHUB_API_URL,
        repository,
        filename
      );
  }

  private String gitHubUrl(String filename) {
    return "%s/%s/blob/main/%s".formatted(GITHUB_URL, repository, filename);
  }

  private HttpResponse<String> send(
    HttpRequest request,
    Integer... expectedResponseCodes
  ) throws InterruptedException {
    try {
      HttpResponse<String> response = client.send(
        request,
        BodyHandlers.ofString()
      );

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
