package com.codeaffine.home.control.test.util.status;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Arrays.asList;

import java.util.Collection;

import com.codeaffine.home.control.status.HomeControlOperation;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.status.StatusSupplier;

public class MyHomeControlOperation implements HomeControlOperation {

  private final MyStatusSupplier myStatusSupplier;

  public MyHomeControlOperation( MyStatusSupplier myStatusSupplier ) {
    verifyNotNull( myStatusSupplier, "myStatusSupplier" );

    this.myStatusSupplier = myStatusSupplier;
  }

  @Override
  public Collection<Class<? extends StatusSupplier<?>>> getRelatedStatusSupplierTypes() {
    return asList( MyStatusSupplier.class );
  }

  @Override
  public void reset() {
  }

  @Override
  public void executeOn( StatusEvent event ) {
  }

  public MyStatusSupplier getMyStatusSupplier() {
    return myStatusSupplier;
  }
}