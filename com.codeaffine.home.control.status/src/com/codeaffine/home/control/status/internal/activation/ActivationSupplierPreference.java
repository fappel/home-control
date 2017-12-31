package com.codeaffine.home.control.status.internal.activation;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

@Preference
public interface ActivationSupplierPreference {

  @DefaultValue( "60" )
  long getPathExpiredTimeoutInSeconds();
  void setPathExpiredTimeoutInSeconds( long value );
}
