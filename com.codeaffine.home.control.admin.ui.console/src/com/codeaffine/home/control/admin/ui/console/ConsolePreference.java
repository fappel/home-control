package com.codeaffine.home.control.admin.ui.console;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

@Preference
public interface ConsolePreference {

  @DefaultValue( "osgi>" )
  String getPrompt();
  void setPrompt( String prompt );
}
