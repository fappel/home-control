package com.codeaffine.home.control.admin.ui.view;

import static com.codeaffine.home.control.admin.ui.control.Banner.newBanner;
import static com.codeaffine.home.control.admin.ui.util.widget.layout.FormDatas.attach;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

import com.codeaffine.home.control.admin.ui.api.ActionSupplier;
import com.codeaffine.home.control.admin.ui.api.Page;
import com.codeaffine.home.control.admin.ui.control.Banner;
import com.codeaffine.home.control.admin.ui.control.Stack;

public class AdminUiView {

  private final AdminUiPreference preference;
  private final ActionSupplier actions;
  private final List<Page> pages;

  private Banner banner;
  private Stack stack;

  public AdminUiView( ActionSupplier actions, AdminUiPreference preference ) {
    verifyNotNull( preference, "preference" );
    verifyNotNull( actions, "actions" );

    this.pages = new CopyOnWriteArrayList<>();
    this.preference = preference;
    this.actions = actions;
  }

  void dispose() {
    pages.forEach( page -> page.dispose() );
    pages.clear();
  }

  void showPage( Object pageId ) {
    verifyNotNull( pageId, "pageId" );

    stack.show( pageId );
  }

  void createContent( Composite parent, List<Page> pages ) {
    verifyNotNull( parent, "parent" );
    verifyNotNull( pages, "pages" );

    this.pages.addAll( pages );
    parent.setLayout( new FormLayout() );
    createComponents( parent );
    initializeContent();
    layoutComponents();
    wireEventHandlers();
  }

  private void createComponents( Composite parent ) {
    banner = createBanner( parent, actions );
    stack = new Stack( parent );
  }

  private void initializeContent() {
    streamOfPages().forEach( page -> {
      banner.getNavigationBar().newItem( page, page.getLabel() );
      stack.newElement( page, elementParent -> page.createContent( elementParent ) );
    } );
  }

  private Stream<Page> streamOfPages() {
    return pages.stream().sorted( ( page1, page2 ) -> compare( page1, page2 ) );
  }

  private int compare( Page page1, Page page2 ) {
    int indexOf1 = preference.getPageOrder().indexOf( page1.getLabel() );
    int indexOf2 = preference.getPageOrder().indexOf( page2.getLabel() );
    return indexOf1 - indexOf2;
  }

  private void layoutComponents() {
    attach( banner.getControl() ).toLeft().toTop().toRight().withHeight( 70 );
    attach( stack.getControl() ).toLeft( 20 ).atTopTo( banner.getControl(), 20 ).toRight( 20 ).toBottom( 20 );
  }

  private void wireEventHandlers() {
    findFirstPage().ifPresent( page -> selectPage( page ) );
  }

  private void selectPage( Page page ) {
    banner.getNavigationBar().selectItem( page );
  }

  private Optional<Page> findFirstPage() {
    return streamOfPages().findFirst();
  }

  private static Banner createBanner( Composite parent, ActionSupplier actionSupplier ) {
    Banner result = newBanner( parent, new FormLayout() )
      .withLogo( "Home Control" )
      .withTitle( "Administration" )
      .withSeparator()
      .withNavigationBar( actionSupplier );
    attach( result.getLogo() ).toLeft( 20 ).toTop( 10 );
    attach( result.getTitle() ).toLeft( 20 ).atTopTo( result.getLogo(), 3 );
    attach( result.getSeparator() ).toLeft().toBottom( 10 ).toRight().withHeight( 1 );
    attach( result.getNavigationBar().getControl() ).toTop().toRight( 20 ).atBottomTo( result.getSeparator() );
    return result;
  }
}