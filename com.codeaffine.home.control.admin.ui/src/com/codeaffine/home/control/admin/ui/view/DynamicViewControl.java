package com.codeaffine.home.control.admin.ui.view;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.codeaffine.home.control.admin.ui.api.PageFactorySupplier;
import com.codeaffine.home.control.event.Subscribe;
import com.codeaffine.home.control.preference.PreferenceEvent;

public class DynamicViewControl {

  static final String PREFERENCE_ATTRIBUTE_PAGE_ORDER = "pageOrder";

  private final PageFactorySupplier pageFactories;
  private final ViewContentLifeCycle lifeCycle;
  private final ServerPushSession serverPush;
  private final UISession uiSession;

  private Runnable updateHook;

  public DynamicViewControl(
    ViewContentLifeCycle lifeCycle, PageFactorySupplier pageFactories, UISession session, ServerPushSession serverPush )
  {
    verifyNotNull( pageFactories, "pageFactories" );
    verifyNotNull( serverPush, "serverPush" );
    verifyNotNull( lifeCycle, "lifeCycle" );
    verifyNotNull( session, "session" );

    this.pageFactories = pageFactories;
    this.serverPush = serverPush;
    this.lifeCycle = lifeCycle;
    this.uiSession = session;
  }

  public void createContent( Composite parent ) {
    verifyNotNull( parent, "parent" );

    serverPush.start();
    lifeCycle.createViewContent( parent );
    updateHook = registerUpdateHook( parent );
    ensureCleanupAtEndOfSession( updateHook );
  }

  @Subscribe
  void onPreferenceChange( PreferenceEvent event ) {
    verifyNotNull( event, "event" );

    if(    event.getSource() instanceof AdminUiPreference
        && event.getAttributeName().equals( PREFERENCE_ATTRIBUTE_PAGE_ORDER ) )
    {
      updateHook.run();
    }
  }

  private Runnable registerUpdateHook( Composite parent ) {
    Display display = parent.getDisplay();
    Runnable result = () -> display.asyncExec( () -> updateViewContent( parent ) );
    pageFactories.registerUpdateHook( result );
    return result;
  }

  private void updateViewContent( Composite parent ) {
    lifeCycle.disposeViewContent();
    lifeCycle.createViewContent( parent );
    parent.layout();
  }

  private void ensureCleanupAtEndOfSession( Runnable updateHook ) {
    uiSession.addUISessionListener( evt -> {
      pageFactories.deregisterUpdateHook( updateHook );
      lifeCycle.disposeViewContent();
    } );
  }
}