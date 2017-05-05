package com.codeaffine.home.control.admin.ui.preference.info;

import java.util.List;
import java.util.Set;

public interface AttributeInfo {
  String getName();
  String getDisplayName();
  Class<?> getAttributeType();
  Set<AttributeAction> getActions();
  List<Class<?>> getGenericTypeParametersOfAttributeType();
}