package com.codeaffine.home.control.engine.activation;

import java.util.function.Consumer;

import com.codeaffine.home.control.ComponentAccessService;
import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.util.ArgumentVerification;

class ComponentAccessServiceImpl implements ComponentAccessService {

  private final SystemExecutor executor;
  private final Supplier supplier;

  private static class Supplier implements ComponentSupplier {

    private final Context delegate;

    public Supplier( Context delegate ) {
      this.delegate = delegate;
    }

    @Override
    public <T> T get( Class<T> key ) {
      return key.cast( delegate.get( key ) );
    }
  }

  ComponentAccessServiceImpl( Context delegate ) {
    this.executor = delegate.get( SystemExecutor.class );
    this.supplier = new Supplier( delegate );
  }

  @Override
  public void execute( Consumer<ComponentSupplier> command ) {
    ArgumentVerification.verifyNotNull( command, "command" );

    executor.execute( () -> command.accept( supplier ) );
  }
}