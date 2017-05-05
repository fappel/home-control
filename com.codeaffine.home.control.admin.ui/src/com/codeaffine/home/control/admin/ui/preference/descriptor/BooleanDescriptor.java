package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.Boolean.*;

import com.codeaffine.home.control.admin.ui.internal.property.ComboBoxPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.preference.info.AttributeInfo;

class BooleanDescriptor implements AttributeDescriptor {

  static final String[] SELECTION_LABELS = new String[] { FALSE.toString(), TRUE.toString() };
  static final int LABEL_INDEX_FALSE = 0;
  static final int LABEL_INDEX_TRUE = 1;

  private final AttributeInfo attributeInfo;

  BooleanDescriptor( AttributeInfo attributeInfo ) {
    verifyNotNull( attributeInfo, "attributeInfo" );

    this.attributeInfo = attributeInfo;
  }

  @Override
  public IPropertyDescriptor createPropertyDescriptor() {
    String name = attributeInfo.getName();
    String displayName = attributeInfo.getDisplayName();
    ComboBoxPropertyDescriptor descriptor = new ComboBoxPropertyDescriptor( name, displayName, SELECTION_LABELS );
    return new AttributePropertyDescriptor( descriptor, attributeInfo );
  }

  @Override
  public Object convertToRepresentationValue( Object value ) {
    verifyNotNull( value, "value" );

    return Integer.valueOf( ( ( Boolean )value ).booleanValue() ? LABEL_INDEX_TRUE : LABEL_INDEX_FALSE );
  }

  @Override
  public Object convertToValue( Object label ) {
    verifyNotNull( label, "label" );

    return ( ( Integer )label ).intValue() == LABEL_INDEX_FALSE ? Boolean.FALSE : Boolean.TRUE;
  }
}