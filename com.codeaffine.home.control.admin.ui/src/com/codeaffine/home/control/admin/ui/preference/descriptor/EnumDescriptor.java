package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Arrays.asList;

import java.util.stream.Stream;

import com.codeaffine.home.control.admin.ui.internal.property.ComboBoxPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

class EnumDescriptor implements AttributeDescriptor {

  private final AttributeInfo attributeInfo;

  EnumDescriptor( AttributeInfo attributeInfo ) {
    verifyNotNull( attributeInfo, "attributeInfo" );

    this.attributeInfo = attributeInfo;
  }

  @Override
  public IPropertyDescriptor createPropertyDescriptor() {
    Class<?> type = attributeInfo.getAttributeType();
    String name = attributeInfo.getName();
    String displayName = attributeInfo.getDisplayName();
    ComboBoxPropertyDescriptor descriptor = new ComboBoxPropertyDescriptor( name, displayName, getEnumLabels( type ) );
    return new AttributePropertyDescriptor( descriptor, attributeInfo );
  }

  @Override
  public Object convertToLabel( Object value ) {
    verifyNotNull( value, "value" );

    Class<?> attributeType = attributeInfo.getAttributeType();
    return Integer.valueOf( asList( attributeType.getEnumConstants() ).indexOf( value ) );
  }

  @Override
  public Object convertToValue( Object label ) {
    verifyNotNull( label, "label" );

    Class<?> attributeType = attributeInfo.getAttributeType();
    return attributeType.getEnumConstants()[ ( ( Integer )label ).intValue() ];
  }

  private static String[] getEnumLabels( Class<?> type ) {
    return Stream.of( type.getEnumConstants() ).map( element -> element.toString() ).toArray( String[]::new );
  }
}