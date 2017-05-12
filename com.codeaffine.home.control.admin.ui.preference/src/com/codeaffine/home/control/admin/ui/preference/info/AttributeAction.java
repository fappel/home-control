package com.codeaffine.home.control.admin.ui.preference.info;

import static com.codeaffine.home.control.admin.ui.preference.info.Messages.ERROR_WRONG_PRESENTATION_TYPE;
import static com.codeaffine.util.ArgumentVerification.*;

public class AttributeAction {

  private final AttributeActionPresentation presentation;
  private final Runnable action;

  public AttributeAction( Runnable action, AttributeActionPresentation presentation ) {
    verifyNotNull( presentation, "presentation" );
    verifyNotNull( action, "action" );

    this.presentation = presentation;
    this.action = action;
  }

  public <T extends AttributeActionPresentation> T getPresentation( Class<T> presentationType ) {
    verifyNotNull( presentationType, "presentationType" );
    verifyCondition( presentationType.isInstance( presentation ),
                     ERROR_WRONG_PRESENTATION_TYPE,
                     presentation.getClass().getName(),
                     presentationType.getName() );

    return presentationType.cast( presentation );
  }

  public void run() {
    action.run();
  }
}