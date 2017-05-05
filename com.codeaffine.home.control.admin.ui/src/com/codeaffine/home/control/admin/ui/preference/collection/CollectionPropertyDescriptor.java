package com.codeaffine.home.control.admin.ui.preference.collection;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;

import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;

public class CollectionPropertyDescriptor extends PropertyDescriptor {

  public CollectionPropertyDescriptor( Object id, String displayName ) {
    super( id, displayName );
  }

  @Override
  public CellEditor createPropertyEditor( Composite parent ) {
    return new CollectionCellEditor( parent );
  }

  @Override
  public ILabelProvider getLabelProvider() {
    return new LabelProvider() {
      @Override
      public String getText( Object element ) {
        return "";
      }
    };
  }
}