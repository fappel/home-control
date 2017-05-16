package com.codeaffine.home.control.admin.ui.view;

import java.util.List;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

@Preference
public interface AdminUiPreference {
  @DefaultValue( "{}" )
  List<String> getPageOrder();
  void setPageOrder( List<String> pageOrder );
}