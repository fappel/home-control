package com.codeaffine.home.control.admin.ui.preference.attribute;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.Boolean.*;

import com.codeaffine.home.control.admin.PreferenceAttributeDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.ComboBoxPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.PreferenceAttributeDescriptorAdapter;

class BooleanAdapter implements PreferenceAttributeDescriptorAdapter {

  static final String[] SELECTION_LABELS = new String[] { FALSE.toString(), TRUE.toString() };
  static final int LABEL_INDEX_FALSE = 0;
  static final int LABEL_INDEX_TRUE = 1;

  private final PreferenceAttributeDescriptor attributeDescriptor;

  BooleanAdapter( PreferenceAttributeDescriptor attributeDescriptor ) {
    verifyNotNull( attributeDescriptor, "attributeDescriptor" );

    this.attributeDescriptor = attributeDescriptor;
  }

  @Override
  public PropertyDescriptor createPropertyDescriptor() {
    String name = attributeDescriptor.getName();
    String displayName = attributeDescriptor.getDisplayName();
    return new ComboBoxPropertyDescriptor( name, displayName, SELECTION_LABELS );
  }

  @Override
  public Object convertToLabel( Object attributeValue ) {
    verifyNotNull( attributeValue, "attributeValue" );

    return Integer.valueOf( ( ( Boolean )attributeValue ).booleanValue() ? LABEL_INDEX_TRUE : LABEL_INDEX_FALSE );
  }

  @Override
  public Object convertToAttributeValue( Object attributeValueLabel ) {
    verifyNotNull( attributeValueLabel, "attributeValueLabel" );

    return ( ( Integer )attributeValueLabel ).intValue() == LABEL_INDEX_FALSE ? Boolean.FALSE : Boolean.TRUE;
  }
}