package com.codeaffine.home.control.admin.ui.view;

import org.eclipse.rap.rwt.client.service.BrowserNavigation;

import com.codeaffine.home.control.admin.ui.api.Page;

class UiActions {

  static void activatePage( Page page, BrowserNavigation navigation, AdminUiView view ) {
    view.showPage( page );
    page.setFocus();
    navigation.pushState( getFragmentId( page ), page.getLabel() );
  }

  static String getFragmentId( Page page ) {
    return page.getLabel();
  }
}