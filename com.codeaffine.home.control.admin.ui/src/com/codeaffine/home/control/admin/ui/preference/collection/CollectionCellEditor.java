package com.codeaffine.home.control.admin.ui.preference.collection;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class CollectionCellEditor extends CellEditor {

  private Object value;
  private Label control;

  public CollectionCellEditor( Composite parent ) {
    super( parent );
  }

  @Override
  protected void doSetValue( Object value ) {
    this.value = value;
    control.setText( "" );
  }

  @Override
  protected void doSetFocus() {
    if( control != null ) {
      control.setFocus();
    }
  }

  @Override
  protected Object doGetValue() {
    return value;
  }

  @Override
  protected Control createControl( Composite parent ) {
    control = new Label( parent, SWT.NONE );
    return control;
  }
}