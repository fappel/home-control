package com.codeaffine.home.control.preference;

public interface PreferenceModel {
  <T> T get( Class<T> preferenceType );
}