package com.codeaffine.home.control.status;

import static com.codeaffine.home.control.type.OnOffType.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.home.control.type.OnOffType;

public class OnOffTypeTest {

  @Test
  public void flip() {
    assertThat( OnOffType.flip( OFF ) ).isSameAs( ON );
    assertThat( OnOffType.flip( ON ) ).isSameAs( OFF );
  }
}