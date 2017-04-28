package com.codeaffine.home.control.admin.ui.preference;

import static com.codeaffine.util.ArgumentVerification.*;

import com.codeaffine.home.control.admin.PreferenceAttributeDescriptor;
import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.IPropertySource;
import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.attribute.PreferenceAttributeDescriptorAdapterSupplier;

class AttributePropertySource implements IPropertySource {

  private final PreferenceAttributeDescriptorAdapterSupplier adapterSupplier;
  private final PreferenceInfo info;

  AttributePropertySource( PreferenceInfo info ) {
    verifyNotNull( info, "info" );

    this.adapterSupplier = new PreferenceAttributeDescriptorAdapterSupplier( info );
    this.info = info;
  }

  @Override
  public void setPropertyValue( Object id, Object value ) {
    verifyCondition( id instanceof String, "Argument 'id' must be an instance of String."  );
    verifyNotNull( id, "id" );

    PreferenceAttributeDescriptorAdapter adapter = adapterSupplier.getAttributeDescriptorAdapter( ( String ) id );
    Object attributeValue = adapter.convertToAttributeValue( value );
    info.setAttributeValue( ( String )id, attributeValue ) ;
  }

  @Override
  public Object getPropertyValue( Object id ) {
    verifyCondition( id instanceof String, "Argument 'id' must be an instance of String."  );
    verifyNotNull( id, "id" );

    Object attributeValue = info.getAttributeValue( ( String )id );
    PreferenceAttributeDescriptorAdapter adapter = adapterSupplier.getAttributeDescriptorAdapter( ( String ) id );
    return adapter.convertToLabel( attributeValue );
  }

  @Override
  public IPropertyDescriptor[] getPropertyDescriptors() {
    return info
      .getAttributeDescriptors()
      .stream()
      .map( descriptor -> createPropertyDescriptor( descriptor ) )
      .toArray( IPropertyDescriptor[]::new );
  }

  @Override
  public boolean isPropertySet( Object id ) {
    verifyNotNull( id, "id" );

    return true;
  }

  @Override
  public Object getEditableValue() {
    return null;
  }

  @Override
  public void resetPropertyValue( Object id ) {
  }

  private PropertyDescriptor createPropertyDescriptor( PreferenceAttributeDescriptor descriptor ) {
    String id = descriptor.getName();
    PreferenceAttributeDescriptorAdapter adapter = adapterSupplier.getAttributeDescriptorAdapter( id );
    return adapter.createPropertyDescriptor();
  }
}