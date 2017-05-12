package com.codeaffine.home.control.admin.ui.view;

import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.codeaffine.home.control.admin.ui.api.PageFactorySupplier;

public class DynamicViewControl {

  private final ServerPushSession serverPushSession;
  private final PageFactorySupplier pageFactories;
  private final ViewContentLifeCycle lifeCycle;
  private final UISession uiSession;

  public DynamicViewControl( ViewContentLifeCycle lifeCycle, PageFactorySupplier pageFactories, UISession uiSession ) {
    this.serverPushSession = new ServerPushSession();
    this.pageFactories = pageFactories;
    this.lifeCycle = lifeCycle;
    this.uiSession = uiSession;
  }

  public void createContent( Composite parent ) {
    activateServerPush();
    lifeCycle.createViewContent( parent );
    registerUpdateHook( parent );
    ensureViewDisposalAtEndOfSession();
  }

  private void activateServerPush() {
    serverPushSession.start();
    uiSession.addUISessionListener( evt -> serverPushSession.stop() );
  }

  private void registerUpdateHook( Composite parent ) {
    Display display = parent.getDisplay();
    Runnable updateHook = () -> display.asyncExec( () -> updateViewContent( parent ) );
    pageFactories.registerUpdateHook( updateHook );
    uiSession.addUISessionListener( evt -> pageFactories.deregisterUpdateHook( updateHook ) );
  }

  private void updateViewContent( Composite parent ) {
    lifeCycle.disposeViewContent();
    lifeCycle.createViewContent( parent );
    parent.layout();
  }

  private boolean ensureViewDisposalAtEndOfSession() {
    return uiSession.addUISessionListener( evt -> lifeCycle.disposeViewContent() );
  }
}