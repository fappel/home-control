package com.codeaffine.home.control.engine.status;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.status.StatusProvider;
import com.codeaffine.home.control.status.StatusProviderRegistry;

public class StatusProviderRegistryImpl implements StatusProviderRegistry {

  private final Context context;

  public StatusProviderRegistryImpl( Context context ) {
    verifyNotNull( context, "context" );

    this.context = context;
  }

  @Override
  public <T extends StatusProvider<?>> void register( Class<T> type, Class<? extends T> implementation ) {
    verifyNotNull( implementation, "implementation" );
    verifyNotNull( type, "type" );

    context.set( type, context.create( implementation ) );
  }

  @Override
  public Context getContext() {
    return context;
  }
}