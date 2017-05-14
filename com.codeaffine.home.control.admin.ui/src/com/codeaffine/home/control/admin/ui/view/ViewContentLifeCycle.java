package com.codeaffine.home.control.admin.ui.view;

import static com.codeaffine.home.control.admin.ui.view.UiActions.activatePage;
import static java.util.stream.Collectors.toList;
import static org.eclipse.swt.SWT.NONE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Composite;

import com.codeaffine.home.control.admin.ui.api.Page;
import com.codeaffine.home.control.admin.ui.api.PageFactorySupplier;
import com.codeaffine.home.control.admin.ui.model.ActionMap;

public class ViewContentLifeCycle {

  private final AtomicReference<Composite> viewControl;
  private final PageFactorySupplier pageFactories;
  private final ActionMap actionMap;
  private final AdminUiView view;
  private final List<Page> pages;

  public ViewContentLifeCycle( AdminUiView view, PageFactorySupplier pageFactories, ActionMap actionMap ) {
    this.viewControl = new AtomicReference<>();
    this.pages = new ArrayList<>();
    this.pageFactories = pageFactories;
    this.actionMap = actionMap;
    this.view = view;
  }

  void createViewContent( Composite parent ) {
    pages.addAll( createPages() );
    mapViewNavigationActions( view, pages );
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

  private void mapViewNavigationActions( AdminUiView view, List<Page> pages ) {
    pages.forEach( page -> {
      actionMap.putAction( page, () -> activatePage( page, view ) );
    } );
  }

  private void unmapViewNavigationActions( List<Page> pages ) {
    pages.forEach( page -> actionMap.removeAction( page ) );
  }
}