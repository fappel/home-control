package com.codeaffine.home.control.admin.app.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;
import com.codeaffine.home.control.preference.PreferenceModel;

public class PreferenceModelImpl implements PreferenceModel {

  private final Map<Class<?>, Object> preferences;

  @Preference
  static interface MockPreference {
    @DefaultValue( "12" )
    int getValue();
    void setValue( int value );
  }

  private static class MockPreferenceImplementation implements MockPreference {

    private int value;

    @Override
    public void setValue( int value ) {
      this.value = value;
    }

    @Override
    public int getValue() {
      return value;
    }
  }

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