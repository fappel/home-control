package com.codeaffine.home.control.internal.event;

public class EventAdapter {

  private final Object eventObject;

  public EventAdapter( Object eventObject ) {
    this.eventObject = eventObject;
  }

  public Object getEventObject() {
    return eventObject;
  }
}