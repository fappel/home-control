package com.codeaffine.home.control.admin.ui.view;

import static java.util.stream.Collectors.toList;
import static org.eclipse.swt.SWT.NONE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.eclipse.swt.widgets.Composite;

import com.codeaffine.home.control.admin.ui.api.Page;
import com.codeaffine.home.control.admin.ui.api.PageFactorySupplier;
import com.codeaffine.home.control.admin.ui.model.ActionMap;

public class ViewContentLifeCycle {

  private final AtomicReference<Composite> viewControl;
  private final PageFactorySupplier pageFactories;
  private final BrowserNavigation browser;
  private final ActionMap actionMap;
  private final AdminUiView view;
  private final List<Page> pages;

  public ViewContentLifeCycle( AdminUiView view, PageFactorySupplier pageFactories, ActionMap actionMap, BrowserNavigation browser ) {
    this.viewControl = new AtomicReference<>();
    this.pages = new ArrayList<>();
    this.pageFactories = pageFactories;
    this.actionMap = actionMap;
    this.browser = browser;
    this.view = view;
  }

  void createViewContent( Composite parent ) {
    pages.addAll( createPages() );
    mapViewNavigationActions( browser, view, pages );
    viewControl.set( new Composite( parent, NONE ) );
    view.createContent( viewControl.get(), pages );
  }

  void disposeViewContent() {
    view.dispose();
    viewControl.get().dispose();
    unmapViewNavigationActions( pages );
    pages.clear();
  }

  private List<Page> createPages() {
    return pageFactories.getPageFactories().stream().map( factory -> factory.create() ).collect( toList() );
  }

  private void mapViewNavigationActions( BrowserNavigation browserNavigation, AdminUiView view, List<Page> pages ) {
    pages.forEach( page -> {
      actionMap.putAction( page.getId(), () -> UiActions.activatePage( page, browserNavigation, view ) );
    } );
  }

  private void unmapViewNavigationActions( List<Page> pages ) {
    pages.forEach( page -> actionMap.removeAction( page.getId() ) );
  }
}