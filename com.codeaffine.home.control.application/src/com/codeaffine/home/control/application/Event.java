package com.codeaffine.home.control.application;


public class Event {

  private final Object source;

  public Event( Object source ) {
    this.source = source;
  }

  public Object getSource() {
    return source;
  }
}
