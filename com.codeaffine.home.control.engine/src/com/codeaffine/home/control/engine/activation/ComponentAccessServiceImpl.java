package com.codeaffine.home.control.engine.activation;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

import com.codeaffine.home.control.ComponentAccessService;
import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.SystemExecutor;

public class ComponentAccessServiceImpl implements ComponentAccessService {

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

  public ComponentAccessServiceImpl( Context delegate ) {
    verifyNotNull( delegate, "delegate" );

    this.executor = delegate.get( SystemExecutor.class );
    this.supplier = new Supplier( delegate );
  }

  @Override
  public void execute( Consumer<ComponentSupplier> command ) {
    verifyNotNull( command, "command" );

    executor.execute( () -> command.accept( supplier ) );
  }

  @Override
  public <T> T submit( Function<ComponentSupplier, T> task ) {
    verifyNotNull( task, "task" );

    try {
      return executor.submit( () -> task.apply( supplier ) ).get();
    } catch( InterruptedException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    } catch( ExecutionException ex ) {
      throw ( RuntimeException )ex.getCause();
    }
  }
}