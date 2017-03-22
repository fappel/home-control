package com.codeaffine.home.control.test.util.status;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Arrays.asList;

import java.util.Collection;

import com.codeaffine.home.control.status.HomeControlOperation;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.status.StatusProvider;

public class MyHomeControlOperation implements HomeControlOperation {

  private final MyStatusProvider myStatusProvider;

  public MyHomeControlOperation( MyStatusProvider myStatusProvider ) {
    verifyNotNull( myStatusProvider, "myStatusProvider" );

    this.myStatusProvider = myStatusProvider;
  }

  @Override
  public Collection<Class<? extends StatusProvider<?>>> getRelatedStatusProviderTypes() {
    return asList( MyStatusProvider.class );
  }

  @Override
  public void reset() {
  }

  @Override
  public void executeOn( StatusEvent event ) {
  }

  public MyStatusProvider getMyStatusProvider() {
    return myStatusProvider;
  }
}