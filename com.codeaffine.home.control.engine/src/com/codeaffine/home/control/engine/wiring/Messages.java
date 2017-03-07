package com.codeaffine.home.control.engine.wiring;

class Messages {
  static final String ERROR_TOO_MANNY_CONFIGURATIONS = "The home control system is limited to one configuration only [loaded: %s, requested: %s].";
  static final String ERROR_WRONG_CONFIGURATION_TO_UNLOAD = "The configuration to unload does not match the loaded configuration  [loaded: %s, requested: %s].";
  static final String ERROR_SCHEDULE_METHOD_WITH_ARGUMENT = "Schedule methods must not have arguments [%s.%s]";
  static final String ERROR_INVALID_PARAMETER_DECLARATION = "Unable to wire the item event observer method <%s> of class <%s>.\nAn item event observer method must have exactly one argument\nwhich must be either of the type <%s> or <%s>.";
}