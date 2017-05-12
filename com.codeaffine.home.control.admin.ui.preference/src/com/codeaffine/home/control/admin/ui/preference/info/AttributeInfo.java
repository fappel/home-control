package com.codeaffine.home.control.admin.ui.preference.info;

import java.util.List;

public interface AttributeInfo {
  String getName();
  String getDisplayName();
  Class<?> getAttributeType();
  List<AttributeAction> getActions();
  List<Class<?>> getGenericTypeParametersOfAttributeType();
}