package com.codeaffine.home.control.engine.adapter;

import static com.codeaffine.home.control.test.util.thread.ExecutorHelper.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.SystemExecutor;

public class ResetHookTriggerTest {

  private ResetHookTrigger trigger;
  private SystemExecutor executor;
  private Runnable hook;

  @Before
  public void setUp() {
    hook = mock( Runnable.class );
    executor = stubInThreadExecutor();
    trigger = new ResetHookTrigger( executor );
    trigger.addResetHook( hook );
  }

  @Test
  public void added() {
    trigger.added( null );

    verify( hook ).run();
  }

  @Test
  public void addedIfExecutorIsBlocked() {
    blockExecutor( executor );

    trigger.added( null );

    verify( hook, never() ).run();
  }

  @Test
  public void addedAfterRemoveResetHook() {
    trigger.removeResetHook( hook );

    trigger.added( null );

    verify( hook, never() ).run();
  }

  @Test
  public void removed() {
    trigger.removed( null );

    verify( hook ).run();
  }

  @Test
  public void removedIfExecutorIsBlocked() {
    blockExecutor( executor );

    trigger.removed( null );

    verify( hook, never() ).run();
  }

  @Test
  public void removedAfterRemoveResetHook() {
    trigger.removeResetHook( hook );

    trigger.removed( null );

    verify( hook, never() ).run();
  }

  @Test
  public void updated() {
    trigger.updated( null, null );

    verify( hook ).run();
  }

  @Test
  public void updatedIfExecutorIsBlocked() {
    blockExecutor( executor );

    trigger.updated( null, null );

    verify( hook, never() ).run();
  }

  @Test
  public void updatedAfterRemoveResetHook() {
    trigger.removeResetHook( hook );

    trigger.updated( null, null );

    verify( hook, never() ).run();
  }
}