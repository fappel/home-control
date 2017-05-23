package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.home.control.util.reflection.ReflectionUtil.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;
import com.codeaffine.home.control.admin.ui.util.viewer.property.ComboBoxPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertyDescriptor;

class PreferenceValueDescriptor implements AttributeDescriptor {

  static final String VALUES_ACCESS_METHOD = "values";

  private final AttributeInfo attributeInfo;

  PreferenceValueDescriptor( AttributeInfo attributeInfo ) {
    verifyNotNull( attributeInfo, "attributeInfo" );

    this.attributeInfo = attributeInfo;
  }

  @Override
  public IPropertyDescriptor createPropertyDescriptor() {
    Class<?> type = attributeInfo.getAttributeType();
    String name = attributeInfo.getName();
    String displayName = attributeInfo.getDisplayName();
    String[] selectionLabels = getSelectionLabels( type );
    ComboBoxPropertyDescriptor descriptor = new ComboBoxPropertyDescriptor( name, displayName, selectionLabels );
    return new ActionBarAdapter( descriptor, attributeInfo );
  }

  @Override
  public Object convertToRepresentationValue( Object value ) {
    verifyNotNull( value, "value" );

    Class<?> attributeType = attributeInfo.getAttributeType();
    return Integer.valueOf( asList( getValues( attributeType ) ).indexOf( value ) );
  }

  @Override
  public Object convertToValue( Object label ) {
    verifyNotNull( label, "label" );

    Class<?> attributeType = attributeInfo.getAttributeType();
    return getValues( attributeType )[ ( ( Integer )label ).intValue() ];
  }

  private static String[] getSelectionLabels( Class<?> type ) {
    return Stream.of( getValues( type ) ).map( value -> value.toString() ).toArray( String[]::new );
  }

  private static Object[] getValues( Class<?> type ) {
    Method valuesMethod = execute( () -> type.getDeclaredMethod( VALUES_ACCESS_METHOD ) );
    return ( Object[] )invoke( valuesMethod, null );
  }
}