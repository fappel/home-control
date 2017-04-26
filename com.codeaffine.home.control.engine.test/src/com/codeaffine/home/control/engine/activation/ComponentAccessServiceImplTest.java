package com.codeaffine.home.control.engine.activation;

import static com.codeaffine.home.control.test.util.thread.ThreadHelper.sleep;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.engine.util.SystemExecutorImpl;
import com.codeaffine.home.control.test.util.context.TestContext;
import com.codeaffine.test.util.lang.ThrowableCaptor;

public class ComponentAccessServiceImplTest {

  private ComponentAccessServiceImpl service;
  private Runnable component;

  @Before
  public void setUp() {
    TestContext context = new TestContext();
    context.set( SystemExecutor.class, new SystemExecutorImpl( newSingleThreadScheduledExecutor() ) );
    component = mock( Runnable.class );
    context.set( Runnable.class, component );
    service = new ComponentAccessServiceImpl( context );
  }

  @Test
  public void execute() {
    service.execute( supplier -> supplier.get( Runnable.class ).run() );
    sleep( 10 );

    verify( component ).run();
  }

  @Test
  public void submit() {
    Runnable actual = service.submit( supplier -> supplier.get( Runnable.class ) );

    assertThat( actual ).isSameAs( component );
  }

  @Test
  public void submitWithNullAsSupplierReturnValue() {
    Optional<Runnable> actual = service.submit( supplier -> null );

    assertThat( actual ).isNull();
  }

  @Test
  public void submitWithProblemInSupplierExecution() {
    RuntimeException problem = new RuntimeException();
    doThrow( problem ).when( component ).run();

    Throwable actual = ThrowableCaptor.thrownBy( () -> service.submit( supplier -> {
      supplier.get( Runnable.class ).run();
      return supplier.get( Runnable.class );
    } ) );

    assertThat( actual ).isSameAs( problem );
  }

  @Test( expected = IllegalArgumentException.class )
  public void submitWithNullAsCommandArgument() {
    service.submit( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void executeWithNullAsCommandArgument() {
    service.execute( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsDelegateArgument() {
    new ComponentAccessServiceImpl( null );
  }
}