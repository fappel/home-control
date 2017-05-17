package com.codeaffine.home.control.admin.ui.view;

import static com.codeaffine.home.control.admin.ui.test.util.DisplayHelper.flushPendingEvents;
import static com.codeaffine.home.control.admin.ui.test.util.ShellHelper.createShell;
import static com.codeaffine.home.control.admin.ui.view.DynamicViewControl.PREFERENCE_ATTRIBUTE_PAGE_ORDER;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import com.codeaffine.home.control.admin.ui.api.PageFactorySupplier;
import com.codeaffine.home.control.admin.ui.test.util.DisplayHelper;
import com.codeaffine.home.control.preference.PreferenceEvent;

public class DynamicViewControlTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private DynamicViewControl dynamicViewControl;
  private PageFactorySupplier pageFactories;
  private ViewContentLifeCycle lifeCycle;
  private ServerPushSession serverPush;
  private UISession session;
  private Shell parent;

  @Before
  public void setUp() {
    parent = createShell( displayHelper );
    lifeCycle = mock( ViewContentLifeCycle.class );
    pageFactories = mock( PageFactorySupplier.class );
    session = mock( UISession.class );
    serverPush = mock( ServerPushSession.class );
    dynamicViewControl = new DynamicViewControl( lifeCycle, pageFactories, session, serverPush );
  }

  @Test
  public void createContent() {
    dynamicViewControl.createContent( parent );

    verify( serverPush ).start();
    verify( lifeCycle ).createViewContent( parent );
    assertThat( captureUpdateHook() ).isNotNull();
  }

  @Test
  public void runUpdateHook() {
    Listener listener = registerResizeObserver( mock( Listener.class ) );
    dynamicViewControl.createContent( parent );

    captureUpdateHook().run();
    flushPendingEvents();

    InOrder order = inOrder( lifeCycle, listener );
    order.verify( lifeCycle ).disposeViewContent();
    order.verify( lifeCycle ).createViewContent( parent );
    order.verify( listener ).handleEvent( any() );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void onPreferenceChange() {
    Listener listener = registerResizeObserver( mock( Listener.class ) );
    AdminUiPreference preference = mock( AdminUiPreference.class );
    dynamicViewControl.createContent( parent );

    dynamicViewControl.onPreferenceChange( newEvent( preference, PREFERENCE_ATTRIBUTE_PAGE_ORDER ) );
    flushPendingEvents();

    InOrder order = inOrder( lifeCycle, listener );
    order.verify( lifeCycle ).disposeViewContent();
    order.verify( lifeCycle ).createViewContent( parent );
    order.verify( listener ).handleEvent( any() );
    order.verifyNoMoreInteractions();
  }

  @Test
  public void onPreferenceChangeOfUnrelatedEventType() {
    Listener listener = registerResizeObserver( mock( Listener.class ) );
    AdminUiPreference preference = mock( AdminUiPreference.class );
    dynamicViewControl.createContent( parent );
    reset( lifeCycle );

    dynamicViewControl.onPreferenceChange( newEvent( preference, "unrelated attribute name" ) );
    flushPendingEvents();

    verify( lifeCycle, never() ).disposeViewContent();
    verify( lifeCycle, never() ).createViewContent( parent );
    verify( listener, never() ).handleEvent( any() );
  }

  @Test
  public void onPreferenceChangeOfUnrelatedEventAttribute() {
    Listener listener = registerResizeObserver( mock( Listener.class ) );
    dynamicViewControl.createContent( parent );
    reset( lifeCycle );

    dynamicViewControl.onPreferenceChange( newEvent( new Object(), PREFERENCE_ATTRIBUTE_PAGE_ORDER ) );
    flushPendingEvents();

    verify( lifeCycle, never() ).disposeViewContent();
    verify( lifeCycle, never() ).createViewContent( parent );
    verify( listener, never() ).handleEvent( any() );
  }

  @Test
  public void cleanupOnUiSessionDestroy() {
    dynamicViewControl.createContent( parent );
    verify( serverPush ).start();
    Runnable updateHook = captureUpdateHook();
    UISessionListener listener = captureUiSessionListener();

    listener.beforeDestroy( new UISessionEvent( session ) );

    InOrder order = inOrder( pageFactories, lifeCycle );
    order.verify( pageFactories ).deregisterUpdateHook( updateHook );
    order.verify( lifeCycle ).disposeViewContent();
    order.verifyNoMoreInteractions();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLifeCycleArgument() {
    new DynamicViewControl( null, pageFactories, session, serverPush );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsPageFactoriesArgument() {
    new DynamicViewControl( lifeCycle, null, session, serverPush );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSessionArgument() {
    new DynamicViewControl( lifeCycle, pageFactories, null, serverPush );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsServerPushArgument() {
    new DynamicViewControl( lifeCycle, pageFactories, session, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createContentWithNullAsParentArgument() {
    dynamicViewControl.createContent( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void onPreferenceChangeWithNullAsEventArgument() {
    dynamicViewControl.onPreferenceChange( null );
  }

  private Listener registerResizeObserver( Listener listener ) {
    parent.setBounds( 10, 20, 300, 200 );
    Composite layoutHook = new Composite( parent, SWT.NONE );
    layoutHook.addListener( SWT.Resize, listener );
    return listener;
  }

  private Runnable captureUpdateHook() {
    ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
    verify( pageFactories ).registerUpdateHook( captor.capture() );
    return captor.getValue();
  }

  private UISessionListener captureUiSessionListener() {
    ArgumentCaptor<UISessionListener> captor = forClass( UISessionListener.class );
    verify( session ).addUISessionListener( captor.capture() );
    return captor.getValue();
  }

  private static PreferenceEvent newEvent( Object source, String attribute ) {
    return new PreferenceEvent( source, attribute, null, emptyList() );
  }
}