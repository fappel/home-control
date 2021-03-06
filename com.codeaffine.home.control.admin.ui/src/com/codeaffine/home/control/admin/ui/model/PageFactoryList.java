package com.codeaffine.home.control.admin.ui.model;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import com.codeaffine.home.control.admin.ui.api.PageFactory;
import com.codeaffine.home.control.admin.ui.api.PageFactorySupplier;

public class PageFactoryList implements PageFactorySupplier {

  private final CopyOnWriteArrayList<PageFactory> factories;
  private final CopyOnWriteArraySet<Runnable> hooks;

  public PageFactoryList() {
    factories = new CopyOnWriteArrayList<>();
    hooks = new CopyOnWriteArraySet<>();
  }

  public void addPageFactory( PageFactory pageFactory ) {
    verifyNotNull( pageFactory, "pageFactory" );

    if( factories.addIfAbsent( pageFactory ) ) {
      notifyUpdateHooks();
    }
  }

  public void removePageFactory( PageFactory pageFactory ) {
    verifyNotNull( pageFactory, "pageFactory" );

    if( factories.remove( pageFactory ) ) {
      notifyUpdateHooks();
    }
  }

  @Override
  public List<PageFactory> getPageFactories() {
    return new ArrayList<>( factories );
  }

  @Override
  public void registerUpdateHook( Runnable updateHook ) {
    verifyNotNull( updateHook, "updateHook" );

    hooks.add( updateHook );
  }

  @Override
  public void deregisterUpdateHook( Runnable updateHook ) {
    verifyNotNull( updateHook, "updateHook" );

    hooks.remove( updateHook );
  }

  private void notifyUpdateHooks() {
    hooks.forEach( hook -> hook.run() );
  }
}