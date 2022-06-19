package com.lescastcodeurs.bot;

import static com.lescastcodeurs.bot.Constants.GITHUB_REPOSITORY;
import static com.lescastcodeurs.bot.Constants.GITHUB_TOKEN;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Base64;
import javax.enterprise.context.ApplicationScoped;
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
   * Create a new file in the configured {@link #repository} under the given filename.
   *
   * @see <a href="https://docs.github.com/en/rest/repos/contents#create-or-update-file-contents">Create or update file contents</a>
   */
  public String createFile(String filename, String content)
    throws InterruptedException {
    send(
      HttpRequest
        .newBuilder(URI.create(gitHubApiUrl(filename)))
        .header("Accept", "application/vnd.github.v3+json")
        .header("Authorization", "token " + token)
        .PUT(
          HttpRequest.BodyPublishers.ofString(
            """
            {
              "message": "publish show notes",
              "committer": { "name": "@lcc", "email": "commentaire@lescastcodeurs.com" },
              "content": "%s"
            }
            """.formatted(
                base64(content)
              )
          )
        )
        .build(),
      201
    );

    return gitHubUrl(filename);
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

  private String base64(String content) {
    return encoder.encodeToString(content.getBytes(UTF_8));
  }

  private HttpResponse<String> send(
    HttpRequest request,
    int expectedResponseCode
  ) throws InterruptedException {
    try {
      HttpResponse<String> response = client.send(
        request,
        BodyHandlers.ofString()
      );

      int status = response.statusCode();
      if (status == expectedResponseCode) {
        return response;
      } else {
        throw new GitHubApiException(response);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
