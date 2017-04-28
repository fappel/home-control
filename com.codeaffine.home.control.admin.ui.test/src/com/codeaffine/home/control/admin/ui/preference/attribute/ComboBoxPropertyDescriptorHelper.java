package com.codeaffine.home.control.admin.ui.preference.attribute;

import com.codeaffine.home.control.admin.ui.internal.property.ComboBoxLabelProvider;
import com.codeaffine.home.control.admin.ui.internal.property.ComboBoxPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.internal.property.PropertyDescriptor;

class ComboBoxPropertyDescriptorHelper {

  static String[] getSelectionValues( PropertyDescriptor descriptor ) {
    return ( ( ComboBoxLabelProvider )( ( ComboBoxPropertyDescriptor )descriptor ).getLabelProvider() ).getValues();
  }
}