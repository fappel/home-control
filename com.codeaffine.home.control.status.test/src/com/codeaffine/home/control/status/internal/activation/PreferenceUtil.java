package com.codeaffine.home.control.status.internal.activation;

import static org.mockito.Mockito.*;

class PreferenceUtil {

  final static long PATH_EXPIRED_TIMEOUT_IN_SECONDS = 60;

  static ActivationSupplierPreference stubPreference(long pathExpiredTimeoutInSeconds) {
    ActivationSupplierPreference result = mock( ActivationSupplierPreference.class );
    when( result.getPathExpiredTimeoutInSeconds() ).thenReturn( pathExpiredTimeoutInSeconds );
    return result;
  }
}