package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;

import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

class AttributePropertyDescriptor implements IPropertyDescriptor {

  private final AttributeCellEditorActionBarFactory cellEditorActionBarFactory;
  private final IPropertyDescriptor delegate;
  private final AttributeInfo info;

  AttributePropertyDescriptor( IPropertyDescriptor delegate, AttributeInfo info ) {
    this( delegate, info, new AttributeCellEditorActionBarFactory() );
  }

  AttributePropertyDescriptor(
    IPropertyDescriptor delegate, AttributeInfo info, AttributeCellEditorActionBarFactory cellEditorActionBarFactory )
  {
    verifyNotNull( cellEditorActionBarFactory, "cellEditorActionBarFactory" );
    verifyNotNull( delegate, "delegate" );
    verifyNotNull( info, "info" );

    this.cellEditorActionBarFactory = cellEditorActionBarFactory;
    this.delegate = delegate;
    this.info = info;
  }

  @Override
  public CellEditor createPropertyEditor( Composite parent ) {
    CellEditor result = delegate.createPropertyEditor( parent );
    if( result != null && !info.getActions().isEmpty() ) {
      cellEditorActionBarFactory.create( parent, result, info.getActions() );
    }
    return result;
  }

  @Override
  public String getCategory() {
    return delegate.getCategory();
  }

  @Override
  public String getDescription() {
    return delegate.getDescription();
  }

  @Override
  public String getDisplayName() {
    return delegate.getDisplayName();
  }

  @Override
  public String[] getFilterFlags() {
    return delegate.getFilterFlags();
  }

  @Override
  public Object getHelpContextIds() {
    return delegate.getHelpContextIds();
  }

  @Override
  public Object getId() {
    return delegate.getId();
  }

  @Override
  public ILabelProvider getLabelProvider() {
    return delegate.getLabelProvider();
  }

  @Override
  public boolean isCompatibleWith( IPropertyDescriptor anotherProperty ) {
    return delegate.isCompatibleWith( anotherProperty );
  }

  IPropertyDescriptor getDelegate() {
    return delegate;
  }
}