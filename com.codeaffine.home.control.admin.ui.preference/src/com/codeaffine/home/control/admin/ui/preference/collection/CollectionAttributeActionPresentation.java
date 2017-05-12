package com.codeaffine.home.control.admin.ui.preference.collection;

import org.eclipse.swt.SWT;

import com.codeaffine.home.control.admin.ui.preference.descriptor.ActionPresentation;

public enum CollectionAttributeActionPresentation implements ActionPresentation {

  UP( SWT.ARROW | SWT.UP, "" ), DOWN( SWT.ARROW | SWT.DOWN, "" ), ADD( SWT.PUSH, "+" ), DELETE( SWT.PUSH, "-" );

  private final String label;
  private final int style;

  private CollectionAttributeActionPresentation( int style, String label ) {
    this.style = style;
    this.label = label;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public int getStyle() {
    return style;
  }
}