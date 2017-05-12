package com.codeaffine.home.control.admin.ui.view;

import org.eclipse.rap.rwt.client.service.BrowserNavigation;

import com.codeaffine.home.control.admin.ui.api.Page;

class UiActions {

  static void activatePage( Page page, BrowserNavigation navigation, AdminUiView view ) {
    view.showPage( page.getId() );
    page.setFocus();
    navigation.pushState( page.getId(), page.getId() );
  }
}