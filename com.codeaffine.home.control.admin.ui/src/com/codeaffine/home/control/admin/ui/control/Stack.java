package com.codeaffine.home.control.admin.ui.control;

import static com.codeaffine.home.control.admin.ui.control.Messages.*;
import static com.codeaffine.util.ArgumentVerification.*;
import static org.eclipse.swt.SWT.NONE;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class Stack {

  private final Map<Object, Composite> elements;
  private final StackLayout layout;
  private final Composite control;

  public Stack( Composite parent ) {
    verifyNotNull( parent, "parent" );

    control = new Composite( parent, NONE );
    layout = new StackLayout();
    elements = new HashMap<>();
    control.setLayout( layout );
  }

  public Control getControl() {
    return control;
  }

  public void newElement( Object elementId, Consumer<Composite> elementFactory ) {
    verifyCondition( !elements.containsKey( elementId ), ERROR_STACK_ELEMENT_ID_ALREADY_EXISTS, elementId );
    verifyNotNull( elementFactory, "elementFactory" );
    verifyNotNull( elementId, "elementId" );

    Composite elementControl = new Composite( control, NONE );
    elementControl.setLayout( new FillLayout() );
    elementFactory.accept( elementControl );
    elements.put( elementId, elementControl );
  }

  public void show( Object elementId ) {
    verifyNotNull( elementId, "elementId" );
    verifyCondition( elements.containsKey( elementId ), ERROR_STACK_ELEMENT_ID_DOES_NOT_EXIST, elementId );

    layout.topControl = elements.get( elementId );
    control.layout();
  }
}