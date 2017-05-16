package com.codeaffine.home.control.engine.component.event;

public class EventAdapter {

  private final Object eventObject;

  public EventAdapter( Object eventObject ) {
    this.eventObject = eventObject;
  }

  public Object getEventObject() {
    return eventObject;
  }
}