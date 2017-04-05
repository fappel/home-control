package com.codeaffine.home.control.engine.status;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.status.StatusSupplier;
import com.codeaffine.home.control.status.StatusSupplierRegistry;

public class StatusProviderRegistryImpl implements StatusSupplierRegistry {

  private final Context context;

  public StatusProviderRegistryImpl( Context context ) {
    verifyNotNull( context, "context" );

    this.context = context;
  }

  @Override
  public <T extends StatusSupplier<?>> void register( Class<T> type, Class<? extends T> implementation ) {
    verifyNotNull( implementation, "implementation" );
    verifyNotNull( type, "type" );

    context.set( type, context.create( implementation ) );
  }

  @Override
  public Context getContext() {
    return context;
  }
}