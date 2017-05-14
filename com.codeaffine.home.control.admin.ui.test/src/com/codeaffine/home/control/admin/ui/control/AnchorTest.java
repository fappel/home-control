package com.codeaffine.home.control.admin.ui.control;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.swt.SWT.Selection;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.eclipse.rap.rwt.client.service.BrowserNavigationEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.admin.ui.test.util.DisplayHelper;
import com.codeaffine.home.control.admin.ui.util.UrlUtil;

public class AnchorTest {

  private static final String ANCHOR_TEXT = "anchorText";
  private static final String CSS_CLASS = "cssClass";
  private static final String FRAGMENT = "fragment";

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private BrowserNavigationSpy browserNavigation;
  private Anchor anchor;

  @Before
  public void setUp() {
    browserNavigation = new BrowserNavigationSpy();
    displayHelper.replaceClient( stubClient( browserNavigation ) );
    anchor = new Anchor( displayHelper.createShell() );
  }

  @Test
  public void configure() {
    anchor.configure( CSS_CLASS, FRAGMENT, ANCHOR_TEXT );

    Control control = anchor.getControl();
    String anchorHtml = anchor.getAnchorHtml();
    assertThat( control.getData( RWT.CUSTOM_VARIANT ) ).isEqualTo( CSS_CLASS );
    assertThat( anchorHtml )
      .isEqualTo( format( Anchor.FRAGMENT_ANCHOR_PATTERN, CSS_CLASS, getServletUrl(), FRAGMENT, ANCHOR_TEXT ) );
  }

  @Test
  public void selectionOfRelatedNavigationFragment() throws Exception {
    Listener listener = mock( Listener.class );
    anchor.configure( CSS_CLASS, FRAGMENT, ANCHOR_TEXT );

    anchor.addListener( Selection, listener );
    simmulateBrowserNavigationEvent( FRAGMENT );

    ArgumentCaptor<Event> captor = forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    assertThat( captor.getValue().widget ).isSameAs( anchor.getControl() );
    assertThat( captor.getValue().type ).isEqualTo( Selection );
    assertThat( browserNavigation.getState() ).isEqualTo( FRAGMENT );
    assertThat( browserNavigation.getTitle() ).isEqualTo( FRAGMENT );
  }

  @Test
  public void selectionOfUnrelatedNavigationFragment() throws Exception {
    Listener listener = mock( Listener.class );
    anchor.configure( CSS_CLASS, FRAGMENT, ANCHOR_TEXT );

    anchor.addListener( Selection, listener );
    simmulateBrowserNavigationEvent( "unrelated fragment" );

    verify( listener, never() ).handleEvent( any() );
  }

  @Test
  public void removeSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    anchor.configure( CSS_CLASS, FRAGMENT, ANCHOR_TEXT );
    anchor.addListener( Selection, listener );

    anchor.removeListener( Selection, listener );
    simmulateBrowserNavigationEvent( FRAGMENT );

    verify( listener, never() ).handleEvent( any() );
  }

  @Test
  public void disposeControl() throws Exception {
    Listener listener = mock( Listener.class );
    anchor.configure( CSS_CLASS, FRAGMENT, ANCHOR_TEXT );
    anchor.addListener( Selection, listener );

    anchor.getControl().dispose();

    assertThat( browserNavigation.getListener() ).isNull();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsParentArgument() {
    new Anchor( null );
  }

  private static String getServletUrl() {
    return new UrlUtil().getServletUrl();
  }

  private void simmulateBrowserNavigationEvent( String fragment ) {
    browserNavigation.getListener().navigated( new BrowserNavigationEvent( browserNavigation, fragment ) );
  }

  private static Client stubClient( BrowserNavigation browserNavigation ) {
    Client result = mock( Client.class );
    when( result.getService( BrowserNavigation.class ) ).thenReturn( browserNavigation );
    return result;
  }
}