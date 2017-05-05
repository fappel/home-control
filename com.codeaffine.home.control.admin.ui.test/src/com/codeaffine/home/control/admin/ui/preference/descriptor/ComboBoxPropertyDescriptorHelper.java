package com.codeaffine.home.control.admin.ui.preference.descriptor;

import org.eclipse.jface.viewers.ILabelProvider;

import com.codeaffine.home.control.admin.ui.internal.property.ComboBoxLabelProvider;
import com.codeaffine.home.control.admin.ui.internal.property.IPropertyDescriptor;

class ComboBoxPropertyDescriptorHelper {

  static String[] getSelectionValues( IPropertyDescriptor descriptor ) {
    ILabelProvider labelProvider = ( ( AttributePropertyDescriptor )descriptor ).getLabelProvider();
    return ( ( ComboBoxLabelProvider )labelProvider ).getValues();
  }
}