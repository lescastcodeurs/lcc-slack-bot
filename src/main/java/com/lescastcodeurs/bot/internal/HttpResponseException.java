package com.lescastcodeurs.bot.internal;

import java.net.http.HttpResponse;

/** Base class for HTTP response with an unexpected (mostly error) status. */
public class HttpResponseException extends RuntimeException {

  public HttpResponseException(String message) {
    super(message);
  }

  public HttpResponseException(String message, Throwable cause) {
    super(message, cause);
  }

  public HttpResponseException(HttpResponse<String> response) {
    super(
        "Got %s code for %s on %s: %s"
            .formatted(
                response.statusCode(),
                response.request().method(),
                response.uri(),
                response.body()));
  }

  public HttpResponseException(HttpResponseException e) {
    super(e.getMessage(), e);
  }
}
