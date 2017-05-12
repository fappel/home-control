package com.codeaffine.home.control.admin.ui.preference.source;

import static com.codeaffine.util.ArgumentVerification.*;

import com.codeaffine.home.control.admin.ui.preference.descriptor.AttributeDescriptor;
import com.codeaffine.home.control.admin.ui.preference.descriptor.AttributeDescriptorSupplier;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.preference.info.ObjectInfo;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertySource;

public class AttributePropertySource implements IPropertySource {

  private final AttributeDescriptorSupplier descriptorSupplier;
  private final ObjectInfo info;

  public AttributePropertySource( ObjectInfo info ) {
    verifyNotNull( info, "info" );

    this.descriptorSupplier = new AttributeDescriptorSupplier( info );
    this.info = info;
  }

  @Override
  public void setPropertyValue( Object id, Object value ) {
    verifyCondition( id instanceof String, "Argument 'id' must be an instance of String."  );
    verifyNotNull( id, "id" );

    AttributeDescriptor descriptor = descriptorSupplier.getDescriptor( id );
    Object attributeValue = descriptor.convertToValue( value );
    info.setAttributeValue( id, attributeValue ) ;
  }

  @Override
  public Object getPropertyValue( Object id ) {
    verifyCondition( id instanceof String, "Argument 'id' must be an instance of String."  );
    verifyNotNull( id, "id" );

    Object attributeValue = info.getAttributeValue( id );
    AttributeDescriptor descriptor = descriptorSupplier.getDescriptor( id );
    return descriptor.convertToRepresentationValue( attributeValue );
  }

  @Override
  public IPropertyDescriptor[] getPropertyDescriptors() {
    return info
      .getAttributeInfos()
      .stream()
      .map( info -> createPropertyDescriptor( info ) )
      .toArray( IPropertyDescriptor[]::new );
  }

  @Override
  public boolean isPropertySet( Object id ) {
    verifyNotNull( id, "id" );

    return true;
  }

  @Override
  public Object getEditableValue() {
    return info.getEditableValue();
  }

  @Override
  public void resetPropertyValue( Object id ) {
  }

  private IPropertyDescriptor createPropertyDescriptor( AttributeInfo attributeInfo ) {
    String id = attributeInfo.getName();
    AttributeDescriptor valueDescriptor = descriptorSupplier.getDescriptor( id );
    return valueDescriptor.createPropertyDescriptor();
  }
}