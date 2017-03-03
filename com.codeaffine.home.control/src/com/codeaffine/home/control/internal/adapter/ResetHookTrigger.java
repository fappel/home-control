package com.codeaffine.home.control.internal.adapter;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.smarthome.core.common.registry.RegistryChangeListener;
import org.eclipse.smarthome.core.items.Item;

import com.codeaffine.home.control.internal.util.SystemExecutorImpl;

class ResetHookTrigger implements RegistryChangeListener<Item> {

  private final Set<Runnable> resetHooks;
  private final SystemExecutorImpl executor;

  ResetHookTrigger( SystemExecutorImpl executor ) {
    this.executor = executor;
    this.resetHooks = new CopyOnWriteArraySet<>();
  }

  void addResetHook( Runnable resetHook ) {
    resetHooks.add( resetHook );
  }

  void removeResetHook( Runnable hook ) {
    resetHooks.remove( hook );
  }

  @Override
  public void updated( Item oldElement, Item element ) {
    executor.executeAsynchronously( () -> resetHooks.forEach( hook -> hook.run() ) );
  }

  @Override
  public void removed( Item element ) {
    executor.executeAsynchronously( () -> resetHooks.forEach( hook -> hook.run() ) );
  }

  @Override
  public void added( Item element ) {
    executor.executeAsynchronously( () -> resetHooks.forEach( hook -> hook.run() ) );
  }
}