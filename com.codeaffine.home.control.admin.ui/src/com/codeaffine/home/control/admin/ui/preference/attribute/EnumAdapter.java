package com.codeaffine.home.control.admin.ui.preference.attribute;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Arrays.asList;

import java.util.stream.Stream;

import com.codeaffine.home.control.admin.PreferenceAttributeDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.ComboBoxPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.PreferenceAttributeDescriptorAdapter;

class EnumAdapter implements PreferenceAttributeDescriptorAdapter {

  private final PreferenceAttributeDescriptor attributeDescriptor;

  EnumAdapter( PreferenceAttributeDescriptor attributeDescriptor ) {
    verifyNotNull( attributeDescriptor, "attributeDescriptor" );

    this.attributeDescriptor = attributeDescriptor;
  }

  @Override
  public PropertyDescriptor createPropertyDescriptor() {
    Class<?> type = attributeDescriptor.getAttributeType();
    String name = attributeDescriptor.getName();
    String displayName = attributeDescriptor.getDisplayName();
    return new ComboBoxPropertyDescriptor( name, displayName, getEnumLabels( type ) );
  }

  @Override
  public Object convertToLabel( Object attributeValue ) {
    verifyNotNull( attributeValue, "attributeValue" );

    Class<?> attributeType = attributeDescriptor.getAttributeType();
    return Integer.valueOf( asList( attributeType.getEnumConstants() ).indexOf( attributeValue ) );
  }

  @Override
  public Object convertToAttributeValue( Object attributeValueLabel ) {
    verifyNotNull( attributeValueLabel, "attributeValueLabel" );

    Class<?> attributeType = attributeDescriptor.getAttributeType();
    return attributeType.getEnumConstants()[ ( ( Integer )attributeValueLabel ).intValue() ];
  }

  private static String[] getEnumLabels( Class<?> type ) {
    return Stream.of( type.getEnumConstants() ).map( element -> element.toString() ).toArray( String[]::new );
  }
}