package com.codeaffine.home.control.admin.ui;

import static com.codeaffine.home.control.admin.ui.UiActions.activatePage;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static org.eclipse.swt.SWT.*;

import java.util.List;
import java.util.Locale;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

import com.codeaffine.home.control.admin.ui.control.ActionMap;

class AdminUiEntryPoint implements EntryPoint {

  private final List<PageContribution> pageContributions;
  private final ActionMap actionMap;

  AdminUiEntryPoint( List<PageContribution> pageContributions ) {
    verifyNotNull( pageContributions, "pageContributions" );

    this.pageContributions = pageContributions;
    this.actionMap = new ActionMap();
  }

  @Override
  public int createUI() {
    configureSession();
    showUi( prepareView() );
    return 0;
  }

  private AdminUiView prepareView() {
    AdminUiView result = new AdminUiView( actionMap, getBrowserNavigation(), pageContributions );
    mapViewNavigationActions( getBrowserNavigation(), result );
    return result;
  }

  private void mapViewNavigationActions( BrowserNavigation browserNavigation, AdminUiView view ) {
    pageContributions.forEach( contribution -> {
      actionMap.putAction( contribution.getId(), () -> activatePage( contribution, browserNavigation, view ) );
    } );
  }

  private static void showUi( AdminUiView view ) {
    Shell shell = createShell( new FillLayout() );
    view.createContent( new Composite( shell, NONE ) );
    shell.open();
  }

  private static void configureSession() {
    Locale.setDefault( Locale.ENGLISH );
    RWT.getUISession().getHttpSession().setMaxInactiveInterval( 0 );
  }

  private static Shell createShell( Layout layout ) {
    Shell result = new Shell( new Display(), NO_TRIM );
    result.setMaximized( true );
    result.setLayout( layout );
    return result;
  }

  private static BrowserNavigation getBrowserNavigation() {
    return RWT.getClient().getService( BrowserNavigation.class );
  }
}