package com.lescastcodeurs.bot.github;

import com.lescastcodeurs.bot.internal.HttpResponseException;
import java.net.http.HttpResponse;

public class GitHubApiException extends HttpResponseException {

  public GitHubApiException(String message, Throwable cause) {
    super(message, cause);
  }

  public GitHubApiException(HttpResponse<String> response) {
    super(response);
  }
}
