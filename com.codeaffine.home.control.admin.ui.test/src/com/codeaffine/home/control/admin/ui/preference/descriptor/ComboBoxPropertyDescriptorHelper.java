package com.codeaffine.home.control.admin.ui.preference.descriptor;

import org.eclipse.jface.viewers.ILabelProvider;

import com.codeaffine.home.control.admin.ui.util.viewer.property.ComboBoxLabelProvider;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertyDescriptor;

class ComboBoxPropertyDescriptorHelper {

  static String[] getSelectionValues( IPropertyDescriptor descriptor ) {
    ILabelProvider labelProvider = ( ( ActionBarAdapter )descriptor ).getLabelProvider();
    return ( ( ComboBoxLabelProvider )labelProvider ).getValues();
  }
}