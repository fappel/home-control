package com.codeaffine.home.control.engine.event;

public class Messages {
  static final String ERROR_INVALID_PARAMETER_DECLARATION = "Unable to register event observer method <%s> of class <%s>.\nAn event observer method must have exactly one argument\ndeclaring the event to watch out for.";
  static final String ERROR_DURING_EVENT_HANDLING_OF_OBSERVER = "Problem during event handling of observer [%s#%s].";
}