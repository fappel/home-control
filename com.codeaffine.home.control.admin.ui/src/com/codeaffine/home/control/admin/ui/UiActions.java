package com.codeaffine.home.control.admin.ui;

import org.eclipse.rap.rwt.client.service.BrowserNavigation;

class UiActions {

  static void activatePage( PageContribution contribution, BrowserNavigation navigation, AdminUiView view ) {
    view.showPage( contribution.getId() );
    contribution.setFocus();
    navigation.pushState( contribution.getId(), contribution.getId() );
  }
}