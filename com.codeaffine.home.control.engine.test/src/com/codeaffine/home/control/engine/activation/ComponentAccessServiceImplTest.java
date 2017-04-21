package com.codeaffine.home.control.engine.activation;

import static com.codeaffine.home.control.engine.adapter.ExecutorHelper.stubInThreadExecutor;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.test.util.context.TestContext;

public class ComponentAccessServiceImplTest {

  private ComponentAccessServiceImpl service;
  private Runnable component;

  @Before
  public void setUp() {
    TestContext context = new TestContext();
    context.set( SystemExecutor.class, stubInThreadExecutor() );
    component = mock( Runnable.class );
    context.set( Runnable.class, component );
    service = new ComponentAccessServiceImpl( context );
  }

  @Test
  public void execute() {
    service.execute( supplier -> supplier.get( Runnable.class ).run() );

    verify( component ).run();
  }

  @Test( expected = IllegalArgumentException.class )
  public void executeWithNullAsCommandArgument() {
    service.execute( null );
  }
}