package com.codeaffine.home.control.preference;

import java.util.Set;

public interface PreferenceModel {
  <T> T get( Class<T> preferenceType );
  Set<Class<?>> getAllPreferenceTypes();
}