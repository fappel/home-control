package com.codeaffine.home.control.admin.ui.control;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.eclipse.rap.rwt.RWT.*;
import static org.eclipse.swt.SWT.*;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.eclipse.rap.rwt.client.service.BrowserNavigationEvent;
import org.eclipse.rap.rwt.client.service.BrowserNavigationListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.codeaffine.home.control.admin.ui.util.UrlUtil;

public class Anchor {

  static final String FRAGMENT_ANCHOR_PATTERN = "<a class=\"%s\" href=\"%s#%s\">%s</a>";

  private final Composite control;
  private final UrlUtil urlUtil;
  private final Label label;

  private String fragment;

  public Anchor( Composite parent ) {
    this.control = new Composite( parent, NONE );
    this.label = new Label( control, NONE );
    this.urlUtil = new UrlUtil();

    control.setLayout( new AnchorLayout( control, label ) );
    control.setBackgroundMode( INHERIT_FORCE );
    label.setData( MARKUP_ENABLED, TRUE );
    registerBrowserNavigationListener();
  }

  public Control getControl() {
    return control;
  }

  public void addListener( int type, Listener listener ) {
    control.addListener( type, listener );
  }

  public void removeListener( int type, Listener listener ) {
    control.removeListener( type, listener );
  }

  public void configure( String cssClass, String fragment, String anchorText ) {
    this.fragment = fragment;
    control.setData( CUSTOM_VARIANT, cssClass );
    label.setData( CUSTOM_VARIANT, cssClass );
    label.setText( createFragmentAnchorHtml( cssClass, urlUtil.getServletUrl(), fragment, anchorText ) );
  }

  String getAnchorHtml() {
    return label.getText();
  }

  private void registerBrowserNavigationListener() {
    BrowserNavigationListener listener = evt -> handleBrowserNavigationEvent( evt );
    getBrowserNavigation().addBrowserNavigationListener( listener );
    control.addListener( Dispose, evt -> getBrowserNavigation().removeBrowserNavigationListener( listener ) );
  }

  private static BrowserNavigation getBrowserNavigation() {
    return RWT.getClient().getService( BrowserNavigation.class );
  }

  private void handleBrowserNavigationEvent( BrowserNavigationEvent evt ) {
    if( evt.getState().equals( fragment ) ) {
      control.notifyListeners( Selection, createEvent() );
      getBrowserNavigation().pushState( fragment, fragment );
    }
  }

  private Event createEvent() {
    Event result = new Event();
    result.widget = control;
    result.type = Selection;
    return result;
  }

  private static String createFragmentAnchorHtml( String cssClass, String servletUrl, String fragment, String label ) {
    return format( FRAGMENT_ANCHOR_PATTERN, cssClass, servletUrl, fragment, label );
  }
}