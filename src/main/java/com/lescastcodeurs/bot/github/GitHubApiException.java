package com.lescastcodeurs.bot.github;

import java.net.URI;
import java.net.http.HttpResponse;

public class GitHubApiException extends RuntimeException {

  private final int status;
  private final URI uri;

  public GitHubApiException(HttpResponse<String> response) {
    super(response.body());
    this.status = response.statusCode();
    this.uri = response.uri();
  }

  public int getStatus() {
    return status;
  }

  public URI getUri() {
    return uri;
  }
}
