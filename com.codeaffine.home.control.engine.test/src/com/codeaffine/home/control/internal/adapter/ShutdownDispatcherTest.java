package com.codeaffine.home.control.internal.adapter;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.internal.adapter.ShutdownDispatcher;

public class ShutdownDispatcherTest {

  private ShutdownDispatcher dispatcher;
  private Runnable hook;

  @Before
  public void setUp() {
    dispatcher = new ShutdownDispatcher();
    hook = mock( Runnable.class );
  }

  @Test
  public void dispatch() {
    dispatcher.addShutdownHook( hook );

    dispatcher.dispatch();

    verify( hook ).run();
  }

  @Test
  public void addShutdownHookTwice() {
    dispatcher.addShutdownHook( hook );

    dispatcher.addShutdownHook( hook );
    dispatcher.dispatch();

    verify( hook ).run();
  }

  @Test
  public void removeShutdownHook() {
    dispatcher.addShutdownHook( hook );

    dispatcher.removeShutdownHook( hook );
    dispatcher.dispatch();

    verify( hook, never() ).run();
  }
}