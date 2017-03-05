package com.codeaffine.home.control.application.internal.control;

class Messages {
  static final String ERROR_NEXT_NODE_ALREADY_EXISTS = "Next node already exists.";
  static final String ERROR_MISSING_SCENE_SELECTION = "Missing scene selection configuration.";
  static final String ERROR_INVALID_SCENE_SELECTION_CONFIGURATION_MISSING_OTHERWISE_SELECT
    = "Invalid scene selection configuration: missing 'otherwiseSelect' branch on nesting level <%s>.";
  static final String ERROR_SUPERFLUOUS_OTHERWISE_SELECT_BRANCH_DETECTED
    = "Superfluous 'otherwiseSelect' branch detected.";
  static final String INFO_SELECTED_SCENE = "Selected Scene: %s";
  static final String ERROR_SCHEDULE_CALLED_OUTSIDE_OF_SCENE_ACTIVATION
    = "Schedule must not be called outside of scene activation.";
}