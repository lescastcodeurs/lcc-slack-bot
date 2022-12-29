package com.lescastcodeurs.bot.conferences;

import com.lescastcodeurs.bot.internal.HttpResponseException;
import java.net.http.HttpResponse;

public class ConferencesException extends HttpResponseException {

  public ConferencesException(String message) {
    super(message);
  }

  public ConferencesException(String message, Exception cause) {
    super(message, cause);
  }

  public ConferencesException(HttpResponse<String> response) {
    super(response);
  }

  public ConferencesException(HttpResponseException e) {
    super(e);
  }
}
