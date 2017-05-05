package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.TextPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

class TextDescriptor implements AttributeDescriptor {

  private final AttributeInfo attributeInfo;

  public TextDescriptor( AttributeInfo attributeInfo ) {
    verifyNotNull( attributeInfo, "attributeInfo" );

    this.attributeInfo = attributeInfo;
  }

  @Override
  public IPropertyDescriptor createPropertyDescriptor() {
    String name = attributeInfo.getName();
    String displayName = attributeInfo.getDisplayName();
    TextPropertyDescriptor descriptor = new TextPropertyDescriptor( name, displayName );
    return new AttributePropertyDescriptor( descriptor, attributeInfo );
  }

  @Override
  public Object convertToRepresentationValue( Object value ) {
    verifyNotNull( value, "value" );

    return String.valueOf( value );
  }

  @Override
  public Object convertToValue( Object label ) {
    verifyNotNull( label, "label" );

    return label;
  }
}