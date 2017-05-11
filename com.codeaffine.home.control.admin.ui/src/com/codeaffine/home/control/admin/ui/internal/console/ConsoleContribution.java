package com.codeaffine.home.control.admin.ui.internal.console;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.codeaffine.home.control.admin.ui.PageContribution;

public class ConsoleContribution implements PageContribution {

  private Control control;

  @Override
  public String getId() {
    return "Console";
  }

  @Override
  public Control createContent( Composite parent ) {
    OSGiConsole console = new OSGiConsole();
    console.create( parent );
    control = console.getControl();
    return control;
  }

  @Override
  public void setFocus() {
    control.setFocus();
  }
}