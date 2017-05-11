package com.codeaffine.home.control.admin.ui.control;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class Stack {

  private final Map<Object, Composite> elements;
  private final StackLayout layout;
  private final Composite control;

  public Stack( Composite parent ) {
    control = new Composite( parent, SWT.NONE );
    layout = new StackLayout();
    elements = new HashMap<>();
    control.setLayout( layout );
  }

  public Control getControl() {
    return control;
  }

  public void newElement( Object elementId, Consumer<Composite> elementFactory ) {
    Composite elementControl = new Composite( control, SWT.NONE );
    elementControl.setLayout( new FillLayout() );
    elementFactory.accept( elementControl );
    elements.put( elementId, elementControl );
  }

  public void show( Object elementId ) {
    layout.topControl = elements.get( elementId );
    control.layout();
  }
}