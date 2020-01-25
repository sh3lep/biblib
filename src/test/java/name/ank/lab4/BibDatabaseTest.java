package name.ank.lab4;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BibDatabaseTest {

  private BibDatabase database;

  @Before
  public void setup() throws IOException {
    try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/references.bib"))) {
      database = new BibDatabase(reader);
    }
  }

  @Test
  public void getFirstEntry() {
    BibEntry first = database.getEntry(0);
    Assert.assertEquals(Types.ARTICLE, first.getType());
    Assert.assertEquals("The semantic web", first.getField(Keys.TITLE));
    Assert.assertNull("Field 'chapter' does not exist", first.getField(Keys.CHAPTER));
  }

  @Test
  public void normalModeDoesNotThrowException() {
    BibConfig cfg = database.getCfg();
    cfg.strict = false;

    BibEntry first = database.getEntry(0);
    for (int i = 0; i < cfg.maxValid + 1; i++) {
      BibEntry unused = database.getEntry(0);
      Assert.assertNotNull("Should not throw any exception @" + i, first.getType());
    }
  }

  @Test
  public void strictModeThrowsException() {
    BibConfig cfg = database.getCfg();
    cfg.strict = true;

    BibEntry first = database.getEntry(0);
    for (int i = 0; i < cfg.maxValid - 1; i++) {
      BibEntry unused = database.getEntry(0);
      Assert.assertNotNull("Should not throw any exception @" + i, first.getType());
    }
    BibEntry unused = database.getEntry(0);
    try {
      first.getType();
      fail();
    } catch (IllegalStateException e) {
      assertTrue(true);
    }
  }

  @Test
  public void shuffleFlag() throws IOException {
    BibConfig cfg = database.getCfg();
    cfg.strict = false;
    cfg.shuffle = true;
    BibDatabase newDatabase;
    boolean isShuffled = false;
    for (int i = 0; i < 10; i++) {
      try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/references.bib"))) {
        newDatabase = new BibDatabase(reader);
      }
      if (!database.getEntry(0).getField(Keys.TITLE).equals(newDatabase.getEntry(0).getField(Keys.TITLE))) {
        isShuffled = true;
        break;
      }
    }
    assertTrue(isShuffled);
  }
}
