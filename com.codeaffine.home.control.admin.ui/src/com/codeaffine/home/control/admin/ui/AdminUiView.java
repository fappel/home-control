package com.codeaffine.home.control.admin.ui;

import static com.codeaffine.home.control.admin.ui.control.Banner.newBanner;
import static com.codeaffine.home.control.admin.ui.internal.util.FormDatas.attach;

import java.util.List;

import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

import com.codeaffine.home.control.admin.ui.control.ActionMap;
import com.codeaffine.home.control.admin.ui.control.Banner;
import com.codeaffine.home.control.admin.ui.control.Stack;

class AdminUiView {

  private final List<PageContribution> pageContributions;
  private final BrowserNavigation browserNavigation;
  private final ActionMap actionMap;

  private Banner banner;
  private Stack stack;

  AdminUiView( ActionMap actionMap, BrowserNavigation browserNavigation, List<PageContribution> pageContributions ) {
    this.actionMap = actionMap;
    this.browserNavigation = browserNavigation;
    this.pageContributions = pageContributions;
  }

  void showPage( String pageId ) {
    stack.show( pageId );
  }

  void createContent( Composite parent ) {
    parent.setLayout( new FormLayout() );
    createComponents( parent );
    initializeContent();
    layoutComponents();
    wireEventHandlers();
  }

  private void createComponents( Composite parent ) {
    banner = createBanner( parent, actionMap );
    stack = new Stack( parent );
  }

  private void initializeContent() {
    pageContributions.forEach( contribution -> {
      banner.getNavigationBar().newItem( contribution.getId(), contribution.getId() );
      stack.newElement( contribution.getId(), elementParent -> contribution.createContent( elementParent ) );
    } );
  }

  private void layoutComponents() {
    attach( banner.getControl() ).toLeft().toTop().toRight().withHeight( 70 );
    attach( stack.getControl() ).toLeft( 20 ).atTopTo( banner.getControl(), 20 ).toRight( 20 ).toBottom( 20 );
  }

  private void wireEventHandlers() {
    browserNavigation.addBrowserNavigationListener( evt -> banner.getNavigationBar().selectItem( evt.getState() ) );
    pageContributions.stream().findFirst().ifPresent( contribution -> banner.getNavigationBar().selectItem( contribution.getId() ) );
  }

  private static Banner createBanner( Composite parent, ActionMap actionMap ) {
    return newBanner( parent, new FormLayout() )
      .withLogo( "Home Control" )
      .withTitle( "Administration" )
      .withSeparator()
      .withNavigationBar( actionMap )
      .layout( banner -> {
        attach( banner.getLogo() ).toLeft( 20 ).toTop( 10 );
        attach( banner.getTitle() ).toLeft( 20 ).atTopTo( banner.getLogo(), 3 );
        attach( banner.getSeparator() ).toLeft().toBottom( 10 ).toRight().withHeight( 1 );
        attach( banner.getNavigationBar().getControl() ).toTop().toRight( 20 ).atBottomTo( banner.getSeparator() );
      } );
  }
}