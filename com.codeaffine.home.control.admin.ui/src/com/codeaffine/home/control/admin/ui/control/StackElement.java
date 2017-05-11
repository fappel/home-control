package com.codeaffine.home.control.admin.ui.control;

import org.eclipse.swt.widgets.Composite;

public class StackElement {

  private final Composite elementControl;

  StackElement( Composite elementControl ) {
    this.elementControl = elementControl;
  }

  Composite getElementControl() {
    return elementControl;
  }
}
