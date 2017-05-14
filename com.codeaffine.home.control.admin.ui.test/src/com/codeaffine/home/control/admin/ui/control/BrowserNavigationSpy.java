package com.codeaffine.home.control.admin.ui.control;

import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.eclipse.rap.rwt.client.service.BrowserNavigationListener;

class BrowserNavigationSpy implements BrowserNavigation {

  private BrowserNavigationListener listener;
  private String state;
  private String title;

  @Override
  public void pushState( String state, String title ) {
    this.state = state;
    this.title = title;
  }

  @Override
  public void addBrowserNavigationListener( BrowserNavigationListener listener ) {
    this.listener = listener;
  }

  @Override
  public void removeBrowserNavigationListener( BrowserNavigationListener listener ) {
    this.listener = null;
  }

  BrowserNavigationListener getListener() {
    return listener;
  }

  String getState() {
    return state;
  }

  String getTitle() {
    return title;
  }
}