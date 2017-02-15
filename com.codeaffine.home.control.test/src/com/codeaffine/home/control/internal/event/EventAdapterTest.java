package com.codeaffine.home.control.internal.event;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class EventAdapterTest {

  @Test
  public void getEventAdapter() {
    Object expected = new Object();
    EventAdapter adapter = new EventAdapter( expected );

    Object actual = adapter.getEventObject();

    assertThat( actual ).isSameAs( expected );
  }
}
