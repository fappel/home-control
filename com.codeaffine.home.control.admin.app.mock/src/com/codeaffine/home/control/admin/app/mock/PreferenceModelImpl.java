package com.codeaffine.home.control.admin.app.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.codeaffine.home.control.preference.PreferenceModel;

public class PreferenceModelImpl implements PreferenceModel {

  private final Map<Class<?>, Object> preferences;

  public PreferenceModelImpl() {
    preferences = new HashMap<>();
    preferences.put( MockPreference.class, new MockPreferenceImplementation() );
  }

  @Override
  public <T> T get( Class<T> preferenceType ) {
    return preferenceType.cast( preferences.get( preferenceType ) );
  }

  @Override
  public Set<Class<?>> getAllPreferenceTypes() {
    return preferences.keySet();
  }
}