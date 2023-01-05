package com.lescastcodeurs.bot.conferences;

import static java.util.Locale.FRANCE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ConferencesTest {

  private static final List<String> EMPTY = List.of();

  @Test
  void contentCanBeNull() {
    assertThrows(NullPointerException.class, () -> new Conferences(null, EMPTY));
  }

  @Test
  void criteriaCannotBeNull() {
    assertThrows(NullPointerException.class, () -> new Conferences("{}}", null));
  }

  @Test
  void withMalformedJson() {
    Conferences confs = new Conferences("a malformed json", EMPTY);

    assertTrue(confs.markdown(FRANCE).contains(NoConferenceMarkdown.MESSAGE));
  }

  @Test
  void withCorrectJson() {
    Conferences confs =
        new Conferences(
            """
      [
        {
          "name": "Devoxx France",
          "date": [
            2147483647,
            2147483647
          ],
          "hyperlink": "https://devoxx.fr/",
          "location": "France (Paris)",
          "misc": ""
        },
        {
          "name": "Monitorama",
          "date": [
            2147483647,
            2147483647
          ],
          "hyperlink": "http://monitorama.com/",
          "location": "USA",
          "misc": ""
        },
        {
          "name": "Best Of Web",
          "date": [
            -2147483648,
            -2147483648
          ],
          "hyperlink": "http://bestofweb.paris/",
          "location": "France",
          "misc": ""
        }
      ]
      """,
            List.of("(France)", "Devoxx"));

    String markdown = confs.markdown(FRANCE);
    assertTrue(markdown.contains("Devoxx"));
  }
}
