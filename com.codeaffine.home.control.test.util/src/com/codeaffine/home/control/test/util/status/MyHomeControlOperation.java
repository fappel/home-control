package com.codeaffine.home.control.test.util.status;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.status.HomeControlOperation;
import com.codeaffine.home.control.status.StatusEvent;

public class MyHomeControlOperation implements HomeControlOperation {

  private final MyStatusProvider myStatusProvider;

  public MyHomeControlOperation( MyStatusProvider myStatusProvider ) {
    verifyNotNull( myStatusProvider, "myStatusProvider" );

    this.myStatusProvider = myStatusProvider;
  }

  @Override
  public void prepare() {
  }

  @Override
  public void executeOn( StatusEvent event ) {
  }

  public MyStatusProvider getMyStatusProvider() {
    return myStatusProvider;
  }
}