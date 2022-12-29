package com.lescastcodeurs.bot.conferences;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lescastcodeurs.bot.MarkdownSerializable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record Conferences(String json, List<String> selectionCriteria)
    implements MarkdownSerializable {

  private static final Logger LOG = LoggerFactory.getLogger(Conferences.class);

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public Conferences {
    requireNonNull(json);
    requireNonNull(selectionCriteria);
  }

  @Override
  public String markdown() {
    LocalDate now = LocalDate.now();

    try {
      return MAPPER.readValue(json, new TypeReference<List<Conference>>() {}).stream()
          .filter(c -> c.isValidCandidate(selectionCriteria, now))
          .map(Conference::markdown)
          .collect(Collectors.joining());
    } catch (JsonProcessingException e) {
      LOG.warn("An error occurred during JSON processing", e);
      return NoConferenceMarkdown.MESSAGE;
    }
  }
}
