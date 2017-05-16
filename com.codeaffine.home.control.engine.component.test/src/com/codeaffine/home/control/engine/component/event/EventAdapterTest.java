package com.codeaffine.home.control.engine.component.event;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.codeaffine.home.control.engine.component.event.EventAdapter;

public class EventAdapterTest {

  @Test
  public void getEventAdapter() {
    Object expected = new Object();
    EventAdapter adapter = new EventAdapter( expected );

    Object actual = adapter.getEventObject();

    assertThat( actual ).isSameAs( expected );
  }
}
