package com.codeaffine.home.control.admin.ui.preference.descriptor;

import com.codeaffine.home.control.admin.ui.preference.info.AttributeActionPresentation;

public interface ActionPresentation extends AttributeActionPresentation {
  String getLabel();
  int getStyle();
}