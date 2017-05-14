package com.codeaffine.home.control.admin.ui.view;

import static org.eclipse.swt.SWT.NONE;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.codeaffine.home.control.admin.ui.api.Page;

class TestPage implements Page {

  private final String label;

  TestPage( String label ) {
    this.label = label;
  }

  private Label control;

  @Override
  public void setFocus() {
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public Control createContent( Composite parent ) {
    control = new Label( parent, NONE );
    return control;
  }

  @Override
  public void dispose() {
    control.dispose();
  }

  Control getControl() {
    return control;
  }
}