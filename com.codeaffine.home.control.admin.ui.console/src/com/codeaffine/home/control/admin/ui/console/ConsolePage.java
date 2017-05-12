package com.codeaffine.home.control.admin.ui.console;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.codeaffine.home.control.admin.ui.api.Page;

public class ConsolePage implements Page {

  private final OSGiConsole console;

  private Control control;

  public ConsolePage() {
    console = new OSGiConsole();
  }

  @Override
  public String getId() {
    return "Console";
  }

  @Override
  public Control createContent( Composite parent ) {
    console.create( parent );
    control = console.getControl();
    return control;
  }

  @Override
  public void setFocus() {
    control.setFocus();
  }

  @Override
  public void dispose() {
    console.dispose();
  }
}